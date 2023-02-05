package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Integer id = 0;

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Optional<User> get(Integer id) {
        return Optional.of(users.get(id));
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с таким id не существует");
            throw new ValidationException("Пользователь с таким id не существует");
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

    private Integer nextId() {
        return ++id;
    }

}
