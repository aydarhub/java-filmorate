package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private Long id = 0L;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Optional<Film> get(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с таким id не существует");
            throw new NotFoundException("Фильм с таким id не существует");
        }
        films.replace(film.getId(), film);
        return film;
    }

    @Override
    public Film add(Film film) {
        film.setId(nextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Integer id) {
        return films.remove(id);
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }



    @Override
    public void like(Film film, Long userId) {
        film.getLikes().add(userId);
    }

    @Override
    public void unlike(Film film, Long userId) {
        film.getLikes().remove(userId);
    }

    @Override
    public Collection<Film> findPopular(Integer count) {
        return films.values().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Long nextId() {
        return ++id;
    }
}
