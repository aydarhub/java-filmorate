package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Optional<User> get(Integer id);

    User update(User user);

    User add(User user);

    User delete(Integer id);

    Collection<User> findAll();

}
