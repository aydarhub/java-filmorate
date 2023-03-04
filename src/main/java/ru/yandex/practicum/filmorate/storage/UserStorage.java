package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Optional<User> get(Long id);

    User update(User user);

    User add(User user);

    User delete(Integer id);

    Collection<User> findAll();

    void sendRequestToFriend(Long userId, Long friendId);

    void acceptRequestFriend(Long userId, Long friendId);

    void removeFromFriends(Long userId, Long friendId);

}
