package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Logger log = LoggerFactory.getLogger(UserStorage.class);
    Optional<User> findById(Long id);

    User update(User user);

    User add(User user);

    User delete(Long id);

    Collection<User> findAll();

    void sendRequestToFriend(Long userId, Long friendId);

    void acceptRequestFriend(Long userId, Long friendId);

    void removeFromFriends(Long userId, Long friendId);

    default void userNotFound(Long userId) {
        log.warn(String.format("Пользователя с таким id = %d не существует", userId));
        throw new NotFoundException(String.format("Пользователя с таким id = %d не существует", userId));
    }

}
