package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("добавление фильма");
        validateFilm(film);

        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма");
        validateFilm(film);

        return filmService.update(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("получение всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film filmById(@PathVariable Long id) {
        log.info(String.format("Получение фильма с id = %d", id));
        return filmService.filmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Long id, @PathVariable Long userId) {
        log.info(String.format("Лайк фильма с id = %d пользователем с id = %d", id, userId));
        filmService.like(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void unlike(@PathVariable Long id, @PathVariable Long userId) {
        log.info(String.format("Удаление лайка с фильма с id = %d пользователем с id = %d", id, userId));
        filmService.unlike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> findPopular(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Получение популярных фильмов");
        return filmService.findPopular(count);
    }

    private void validateFilm(Film film) {
        if (film == null) {
            log.warn("фильм не может быть null");
            throw new ValidationException("фильм не может быть null");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("название не может быть пустым");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Максимальная длина описания - 200 символов");
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Дата релиза не должно быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не должно быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

}
