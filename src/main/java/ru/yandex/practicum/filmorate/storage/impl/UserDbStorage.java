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
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> get(Long id) {
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
                case "friend":
                    friends.put(user1Id == id ? user2Id : user1Id, FriendStatus.FRIEND);
                    break;
                case "not_approved":
                    friends.put(user1Id == id ? user2Id : user1Id,
                            user1Id == id ? FriendStatus.NOT_APPROVED_OUTGOING : FriendStatus.NOT_APPROVED_INCOMING);
                    break;
                case "declined":
                    friends.put(user1Id == id ? user2Id : user1Id,
                            user1Id == id ? FriendStatus.NOT_APPROVED_OUTGOING : FriendStatus.DECLINED);

            }
        }
        return friends;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public User add(User user) {
        return null;
    }

    @Override
    public User delete(Integer id) {
        return null;
    }

    @Override
    public Collection<User> findAll() {
        return null;
    }

    @Override
    public void sendRequestToFriend(Long userId1, Long userId2) {

    }

    @Override
    public void acceptRequestFriend(Long userId, Long friendId) {

    }

    @Override
    public void removeFromFriends(Long userId, Long friendId) {

    }
}
