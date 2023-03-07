package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User findById(Long id) {
        return userStorage.findById(id).orElseThrow(() -> {
            log.warn(String.format("Пользователя с таким id = %d не существует", id));
            throw new NotFoundException(String.format("Пользователя с таким id = %d не существует", id));
        });
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public void addToFriend(Long userId, Long friendId) {
        if (findById(userId).getFriends().containsKey(friendId)) {
            acceptRequestFriend(userId, friendId);
        } else {
            sendRequestToFriend(userId, friendId);
        }
    }

    private void sendRequestToFriend(Long userId, Long friendId) {
        userStorage.sendRequestToFriend(userId, friendId);
    }

    private void acceptRequestFriend(Long userId, Long friendId) {
        userStorage.acceptRequestFriend(userId, friendId);
    }

    public void removeFromFriends(Long userId, Long friendId) {
        userStorage.removeFromFriends(userId, friendId);
    }

    /*
     * Сначала по невнимательности реализовал подтверждение дружбы.
     * Потом подогнал под тесты, что пользователь считается другом
     * не только когда дружба подтверждена (FriendStatus.FRIEND), но также если есть
     * неподтвержденная исходящая заявка (FriendStatus.NOT_APPROVED_OUTGOING).
     */
    public List<User> findUserFriends(Long id) {
        User user = findById(id);
        return user.getFriends().keySet().stream()
                .filter(friendId -> user.getFriends().get(friendId).equals(FriendStatus.FRIEND)
                        || user.getFriends().get(friendId).equals(FriendStatus.NOT_APPROVED_OUTGOING))
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> findCommonFriends(Long userId1, Long userId2) {
        return findUserFriends(userId1).stream()
                .filter(findUserFriends(userId2)::contains)
                .collect(Collectors.toList());
    }

}
