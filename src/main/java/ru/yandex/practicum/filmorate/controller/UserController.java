package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("добавление пользователя");
        validateUser(user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("обновление пользователя");
        validateUser(user);
        return userService.update(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("получение списка всех пользователей");
        return userService.findAll();
    }

    private void validateUser(User user) {
        if (user == null) {
            log.warn("Пользователь не может быть null");
            throw new ValidationException("Пользователь не может быть null");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("некорректный email");
            throw new ValidationException("некорректный email");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("некорректный логин");
            throw new ValidationException("некорректный логин");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("логин не может быть пустым и содержать пробелы");
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("запись в поле имени значение логина, если полученное имя пустое");
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("дата рождения не может быть в будущем");
            throw new ValidationException("дата рождения не может быть в будущем");
        }
    }

}
