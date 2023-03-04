package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private Long id = 0L;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            userNotFound(user.getId());
        }
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public User add(User user) {
        user.setId(nextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Integer id) {
        return users.remove(id);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public void sendRequestToFriend(Long userId, Long friendId) {
        Map<Long, FriendStatus> userFriends = userFriendsById(userId);
        Map<Long, FriendStatus> friendFriends = userFriendsById(friendId);
        userFriends.put(friendId, FriendStatus.NOT_APPROVED_OUTGOING);
        friendFriends.put(userId, FriendStatus.NOT_APPROVED_INCOMING);
    }

    @Override
    public void acceptRequestFriend(Long userId, Long friendId) {
        Map<Long, FriendStatus> userFriends = userFriendsById(userId);
        Map<Long, FriendStatus> friendFriends = userFriendsById(friendId);

        if (userFriends.containsKey(friendId)
                && userFriends.get(friendId).equals(FriendStatus.NOT_APPROVED_INCOMING)) {
            userFriends.replace(friendId, FriendStatus.FRIEND);
            friendFriends.replace(userId, FriendStatus.FRIEND);
        }
    }

    @Override
    public void removeFromFriends(Long userId, Long friendId) {
        Map<Long, FriendStatus> userFriends = userFriendsById(userId);
        Map<Long, FriendStatus> friendFriends = userFriendsById(friendId);
        if (userFriends.containsKey(friendId)) {
            userFriends.replace(friendId, FriendStatus.DECLINED);
            if (friendFriends.get(userId).equals(FriendStatus.FRIEND)) {
                friendFriends.replace(userId, FriendStatus.NOT_APPROVED_OUTGOING);
            }
        }

        if (userFriends.get(friendId).equals(FriendStatus.DECLINED)
                && friendFriends.get(userId).equals(FriendStatus.DECLINED)) {
            userFriends.remove(friendId);
            friendFriends.remove(userId);
        }

    }

    private Map<Long, FriendStatus> userFriendsById(Long userId) {
        Optional<User> user = get(userId);
        if (user.isEmpty()) {
            userNotFound(userId);
        }
        return user.get().getFriends();

    }


    private void userNotFound(Long userId) {
        log.warn(String.format("Пользователя с таким id = %d не существует", userId));
        throw new NotFoundException(String.format("Пользователя с таким id = %d не существует", userId));
    }

    private Long nextId() {
        return ++id;
    }

}
