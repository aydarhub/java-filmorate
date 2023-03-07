package ru.yandex.practicum.filmorate.storage.impl.dao;

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

    @Override
    public Optional<User> findById(Long id) {
        String userSql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(userSql, (rs, rowNum) -> makeUser(rs), id);
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
        String friendsSql = "SELECT * FROM friendship WHERE user1_id = ? OR user2_id = ?";
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(friendsSql, id, id);

        Map<Long, FriendStatus> friends = new HashMap<>();
        while (friendRows.next()) {
            String status = friendRows.getString("status");
            long user1Id = friendRows.getLong("user1_id");
            long user2Id = friendRows.getLong("user2_id");
            switch (status) {
                case "FRIEND":
                    friends.put(user1Id == id ? user2Id : user1Id, FriendStatus.FRIEND);
                    break;
                case "NOT_APPROVED":
                    friends.put(user1Id == id ? user2Id : user1Id,
                            user1Id == id ? FriendStatus.NOT_APPROVED_OUTGOING : FriendStatus.NOT_APPROVED_INCOMING);
                    break;
                case "DECLINED":
                    friends.put(user1Id == id ? user2Id : user1Id,
                            user1Id == id ? FriendStatus.NOT_APPROVED_OUTGOING : FriendStatus.DECLINED);

            }
        }
        return friends;
    }

    @Override
    public User update(User user) {
        String usersSql = "SELECT user_id FROM users WHERE user_id = ?";
        SqlRowSet usersRows = jdbcTemplate.queryForRowSet(usersSql, user.getId());
        if (!usersRows.next()) {
            userNotFound(user.getId());
        }

        String updateQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(updateQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public User add(User user) {
        String insertQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        String lastIdQuery = "SELECT MAX(user_id) last_id FROM users";
        SqlRowSet lastIdRow = jdbcTemplate.queryForRowSet(lastIdQuery);
        if (lastIdRow.next()) {
            user.setId(lastIdRow.getLong("last_id"));
        }

        return user;
    }

    @Override
    public User delete(Long id) {
        Optional<User> user = findById(id);
        String deleteQuery = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(deleteQuery, id);

        return user.orElse(null);
    }

    @Override
    public Collection<User> findAll() {
        String usersSql = "SELECT * FROM users";

        return jdbcTemplate.query(usersSql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public void sendRequestToFriend(Long userId, Long friendId) {
        if (findById(userId).isEmpty()) {
            userNotFound(userId);
        }
        if (findById(friendId).isEmpty()) {
            userNotFound(friendId);
        }
        String insertQuery = "INSERT INTO friendship (user1_id, user2_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertQuery, userId, friendId, "NOT_APPROVED");
    }

    @Override
    public void acceptRequestFriend(Long userId, Long friendId) {
        if (findById(userId).isEmpty()) {
            userNotFound(userId);
        }
        if (findById(friendId).isEmpty()) {
            userNotFound(friendId);
        }
        String updateQuery = "UPDATE friendship SET status = ? WHERE user1_id = ? AND user2_id = ?";
        jdbcTemplate.update(updateQuery, "FRIEND", friendId, userId);
    }

    @Override
    public void removeFromFriends(Long userId, Long friendId) {
        String friendshipQuery = "SELECT * FROM friendship " +
                "WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(friendshipQuery, userId, friendId, friendId, userId);

        if (friendRows.next()) {
            String status = friendRows.getString("status");
            long user1Id = friendRows.getLong("user1_id");

            String deleteQuery;
            if (user1Id == userId) {
                if (Objects.equals(status, "FRIEND")) {
                    deleteQuery = "UPDATE friendship " +
                            "SET user1_id = user2_id, user2_id = user1_id, status = 'DECLINED'";
                    jdbcTemplate.update(deleteQuery);
                } else {
                    deleteQuery = "DELETE FROM friendship " +
                            "WHERE (user1_id = ? AND user2_id = ?)";
                    jdbcTemplate.update(deleteQuery, userId, friendId);
                }
            } else {
                deleteQuery = "UPDATE friendship " +
                        "SET status = 'DECLINED'";
                jdbcTemplate.update(deleteQuery);
            }
        }
    }
}
