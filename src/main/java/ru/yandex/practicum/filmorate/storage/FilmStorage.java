package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> findById(Long id);

    Film update(Film film);

    Film add(Film film);

    Film delete(Long id);

    Collection<Film> findAll();

    void like(Film film, Long userId);

    void unlike(Film film, Long userId);

    Collection<Film> findPopular(Integer count);

}
