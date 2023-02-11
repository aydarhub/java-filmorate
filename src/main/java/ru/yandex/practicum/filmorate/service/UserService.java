package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User userById(Long id) {
        return userStorage.get(id).orElseThrow(() -> {
            log.warn(String.format("Пользователя с таким id = %d не существует", id));
            throw new NotFoundException(String.format("Пользователя с таким id = %d не существует", id));
        });
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public void addToFriends(Long userId1, Long userId2) {
        userStorage.addToFriends(userId1, userId2);
    }

    public void removeFromFriends(Long userId1, Long userId2) {
        userStorage.removeFromFriends(userId1, userId2);
    }

    public List<User> findUserFriends(Long id) {
        User user = userById(id);
        return user.getFriends().stream()
                .map(userStorage::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> findCommonFriends(Long userId1, Long userId2) {
        User user1 = userById(userId1);
        User user2 = userById(userId2);
        return user1.getFriends().stream()
                .filter(id -> user2.getFriends().contains(id))
                .map(userStorage::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
