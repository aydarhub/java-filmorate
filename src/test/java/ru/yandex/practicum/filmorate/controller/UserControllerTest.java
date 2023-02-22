package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private static UserStorage userStorage;
    private static UserService userService;
    private static UserController userController;
    private User user;

    @BeforeAll
    public static void createController() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @BeforeEach
    public void createFilm() {
        user = new User(0l, "aydarhub@yandex.ru", "login", "name",
                LocalDate.of(1999, Month.DECEMBER, 11));
    }

    @Test
    void shouldExceptionWithNull() {
        ValidationException e = assertThrows(ValidationException.class, () -> userController.create(null));
        assertEquals("Пользователь не может быть null", e.getMessage());
    }

    @Test
    void shouldExceptionWithEmptyLogin() {
        user.setLogin("");
        ValidationException e = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("логин не может быть пустым и содержать пробелы", e.getMessage());
    }

    @Test
    void shouldExceptionWithIncorrectLogin() {
        user.setLogin("log in");
        ValidationException e = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("некорректный логин", e.getMessage());
    }

    @Test
    void shouldExceptionWithIncorrectEmail() {
        user.setEmail("ddd.dddfdddd");
        ValidationException e = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("некорректный email", e.getMessage());
    }

    @Test
    void shouldExceptionUpdateWithNonContainsId() {
        user.setId(10l);
        NotFoundException e = assertThrows(NotFoundException.class, () -> userController.update(user));
        assertEquals("Пользователя с таким id = 10 не существует", e.getMessage());
    }

    @Test
    void shouldNotExceptionWithEmptyName() {
        user.setName("");
        userController.create(user);
        assertEquals(user.getName(), user.getLogin());
    }

}