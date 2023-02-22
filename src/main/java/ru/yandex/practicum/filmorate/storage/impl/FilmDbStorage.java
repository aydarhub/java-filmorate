package ru.yandex.practicum.filmorate.storage.impl;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Optional;

public class FilmDbStorage implements FilmStorage {
    @Override
    public Optional<Film> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public Film add(Film film) {
        return null;
    }

    @Override
    public Film delete(Integer id) {
        return null;
    }

    @Override
    public Collection<Film> findAll() {
        return null;
    }

    @Override
    public void like(Film film, Long userId) {

    }

    @Override
    public void unlike(Film film, Long userId) {

    }

    @Override
    public Collection<Film> findPopular(Integer count) {
        return null;
    }
}
