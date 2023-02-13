package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Optional<User> get(Long id);

    User update(User user);

    User add(User user);

    User delete(Integer id);

    Collection<User> findAll();

    void addToFriends(Long userId1, Long userId2);

    void removeFromFriends(Long userId1, Long userId2);

}
