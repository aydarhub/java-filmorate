package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String SELECT_USER_FRIENDS = "SELECT * FROM friendship WHERE user1_id = ? OR user2_id = ?";
    private static final String UPDATE_USER_BY_ID = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String INSERT_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String SELECT_LAST_USER_ID = "SELECT MAX(user_id) last_id FROM users";
    private static final String DELETE_USER_BY_ID = "DELETE FROM users WHERE user_id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String SEND_REQUEST_TO_FRIEND = "INSERT INTO friendship (user1_id, user2_id, status) VALUES (?, ?, ?)";
    private static final String ACCEPT_REQUEST_FRIEND = "UPDATE friendship SET status = ? WHERE user1_id = ? AND user2_id = ?";
    private static final String FRIENDSHIP_QUERY = "SELECT * FROM friendship " +
            "WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
    private static final String UPDATE_FRIENDSHIP_STATUS_DECLINED_AND_REVERSE = "UPDATE friendship " +
            "SET user1_id = user2_id, user2_id = user1_id, status = 'DECLINED' " +
            "WHERE user1_id = ? AND user2_id = ?";
    private static final String DELETE_FRIENDSHIP = "DELETE FROM friendship " +
            "WHERE (user1_id = ? AND user2_id = ?)";
    private static final String UPDATE_FRIENDSHIP_STATUS_DECLINED = "UPDATE friendship " +
            "SET status = 'DECLINED' " +
            "WHERE user1_id = ? AND user2_id = ?";

    @Override
    public Optional<User> findById(Long id) {
        List<User> users = jdbcTemplate.query(SELECT_USER_BY_ID, (rs, rowNum) -> makeUser(rs), id);
        if (users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(users.get(0));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        User user = new User(id, email, login, name, birthday);
        Map<Long, FriendStatus> friends = findFriends(id);

        user.getFriends().putAll(friends);

        return user;
    }

    // в зависимости в какой колонке таблицы находится id юзера,
    // определяется входящая или исходящая заявка в друзья
    private Map<Long, FriendStatus> findFriends(Long id) {
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(SELECT_USER_FRIENDS, id, id);

        Map<Long, FriendStatus> friends = new HashMap<>();
        while (friendRows.next()) {
            defineFriendshipStatus(friendRows, friends, id);
        }
        return friends;
    }

    private void defineFriendshipStatus(SqlRowSet friendRows, Map<Long, FriendStatus> friends, Long userId) {
        String status = friendRows.getString("status");
        long user1Id = friendRows.getLong("user1_id");
        long user2Id = friendRows.getLong("user2_id");
        switch (status) {
            case "FRIEND":
                friends.put(user1Id == userId ? user2Id : user1Id, FriendStatus.FRIEND);
                break;
            case "NOT_APPROVED":
                friends.put(user1Id == userId ? user2Id : user1Id,
                        user1Id == userId ? FriendStatus.NOT_APPROVED_OUTGOING : FriendStatus.NOT_APPROVED_INCOMING);
                break;
            case "DECLINED":
                friends.put(user1Id == userId ? user2Id : user1Id,
                        user1Id == userId ? FriendStatus.NOT_APPROVED_OUTGOING : FriendStatus.DECLINED);

        }
    }

    @Override
    public User update(User user) {
        SqlRowSet usersRows = jdbcTemplate.queryForRowSet(SELECT_USER_BY_ID, user.getId());
        if (!usersRows.next()) {
            userNotFound(user.getId());
        }
        jdbcTemplate.update(UPDATE_USER_BY_ID, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public User add(User user) {
        jdbcTemplate.update(INSERT_USER, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        SqlRowSet lastIdRow = jdbcTemplate.queryForRowSet(SELECT_LAST_USER_ID);
        if (lastIdRow.next()) {
            user.setId(lastIdRow.getLong("last_id"));
        }

        return user;
    }

    @Override
    public User delete(Long id) {
        Optional<User> user = findById(id);
        jdbcTemplate.update(DELETE_USER_BY_ID, id);

        return user.orElse(null);
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query(SELECT_ALL_USERS, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public void sendRequestToFriend(Long userId, Long friendId) {
        if (findById(userId).isEmpty()) {
            userNotFound(userId);
        }
        if (findById(friendId).isEmpty()) {
            userNotFound(friendId);
        }
        jdbcTemplate.update(SEND_REQUEST_TO_FRIEND, userId, friendId, "NOT_APPROVED");
    }

    @Override
    public void acceptRequestFriend(Long userId, Long friendId) {
        if (findById(userId).isEmpty()) {
            userNotFound(userId);
        }
        if (findById(friendId).isEmpty()) {
            userNotFound(friendId);
        }
        jdbcTemplate.update(ACCEPT_REQUEST_FRIEND, "FRIEND", friendId, userId);
    }

    @Override
    public void removeFromFriends(Long userId, Long friendId) {
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(FRIENDSHIP_QUERY, userId, friendId, friendId, userId);

        if (friendRows.next()) {
            String status = friendRows.getString("status");
            long user1Id = friendRows.getLong("user1_id");
            long user2Id = friendRows.getLong("user2_id");

            String deleteQuery;
            if (user1Id == userId) {
                if (Objects.equals(status, "FRIEND")) {
                    jdbcTemplate.update(UPDATE_FRIENDSHIP_STATUS_DECLINED_AND_REVERSE, user1Id, user2Id);
                } else {
                    jdbcTemplate.update(DELETE_FRIENDSHIP, userId, friendId);
                }
            } else {
                jdbcTemplate.update(UPDATE_FRIENDSHIP_STATUS_DECLINED, user2Id, user1Id);
            }
        }
    }
}
