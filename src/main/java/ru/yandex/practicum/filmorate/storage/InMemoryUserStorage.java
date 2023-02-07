package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Long id = 0l;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> get(Long id) {
        return Optional.of(users.get(id));
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
    public void addToFriends(Long userId1, Long userId2) {
        Optional<User> user1 = get(userId1);
        Optional<User> user2 = get(userId2);
        if (user1.isEmpty()) {
            userNotFound(userId1);
        }
        if (user2.isEmpty()) {
            userNotFound(userId2);
        }
        user1.get().getFriends().add(userId2);
        user2.get().getFriends().add(userId1);
    }

    @Override
    public void removeFromFriends(Long userId1, Long userId2) {
        Optional<User> user1 = get(userId1);
        Optional<User> user2 = get(userId2);
        user1.get().getFriends().remove(userId2);
        user2.get().getFriends().remove(userId1);
    }


    private void userNotFound(Long userId) {
        log.warn(String.format("Пользователя с таким id = %d не существует", userId));
        throw new UserNotFoundException(String.format("Пользователя с таким id = %d не существует", userId));
    }

    private Long nextId() {
        return ++id;
    }

}
