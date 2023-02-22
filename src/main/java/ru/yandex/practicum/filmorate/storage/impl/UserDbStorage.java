package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    @Override
    public Optional<User> get(Long id) {
        return Optional.empty();
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
    public void addToFriends(Long userId1, Long userId2) {

    }

    @Override
    public void removeFromFriends(Long userId1, Long userId2) {

    }
}
