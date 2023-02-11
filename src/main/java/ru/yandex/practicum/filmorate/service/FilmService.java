package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film create(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film filmById(Long id) {
        return filmStorage.get(id).orElseThrow(() -> {
            log.warn(String.format("Фильма с таким id = %d не существует", id));
            throw new NotFoundException(String.format("Фильма с таким id = %d не существует", id));
        });
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Collection<Film> findPopular(Integer count) {
        return filmStorage.findPopular(count);
    }

    public void like(Long id, Long userId) {
        Film film = filmById(id);
        User user = userService.userById(userId);
        filmStorage.like(film, user.getId());
    }

    public void unlike(Long id, Long userId) {
        Film film = filmById(id);
        User user = userService.userById(userId);
        filmStorage.unlike(film, user.getId());
    }

}
