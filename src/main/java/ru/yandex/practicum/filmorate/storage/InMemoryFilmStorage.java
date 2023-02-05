package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Integer id = 0;

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Optional<Film> get(Integer id) {
        return Optional.of(films.get(id));
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с таким id не существует");
            throw new ValidationException("Фильм с таким id не существует");
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

    private Integer nextId() {
        return ++id;
    }
}
