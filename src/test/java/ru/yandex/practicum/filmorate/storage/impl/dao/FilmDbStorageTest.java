package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    public void testFindById() {
        Optional<Film> filmOptional = filmStorage.findById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void testUpdate() {
        Optional<Film> filmOptional = filmStorage.findById(1L);
        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();
            film.setName("Безымяный");
            filmStorage.update(film);
        }
        Optional<Film> filmOptional1 = filmStorage.findById(1L);
        assertThat(filmOptional1)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "Безымяный"));
    }

    @Test
    public void testAdd() {
        Film film = new Film(null, "Film1", "Film1 description",
                LocalDate.of(2010, Month.MAY, 11), 4234, Collections.emptyList(),
                null, new Mpa(1, "G"));
        Film film1 = filmStorage.add(film);
        Optional<Film> filmOptional = filmStorage.findById(film1.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "Film1"));
    }

    @Test
    public void testDelete() {
        filmStorage.delete(2L);
        Optional<Film> filmOptional = filmStorage.findById(2L);
        assertThat(filmOptional).isEmpty();
    }

    @Test
    public void testLike() {
        Optional<Film> filmOptional = filmStorage.findById(1L);
        filmOptional.ifPresent(film -> filmStorage.like(film, 7L));
        Optional<Film> filmOptional1 = filmStorage.findById(1L);
        assertTrue(filmOptional1.get().getLikes().contains(7L));
    }

    @Test
    public void testUnlike() {
        Optional<Film> filmOptional = filmStorage.findById(1L);
        filmOptional.ifPresent(film -> filmStorage.like(film, 7L));
        filmOptional.ifPresent(film -> filmStorage.unlike(film, 7L));
        Optional<Film> filmOptional1 = filmStorage.findById(1L);
        assertFalse(filmOptional1.get().getLikes().contains(7L));
    }

}