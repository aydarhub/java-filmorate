package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/testdata.sql"})
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Test
    public void testFindAll() {
        ArrayList<Genre> genres = (ArrayList<Genre>) genreStorage.findAll();

        assertEquals(13, genres.size());
        assertEquals(genres.get(0).getName(), "Комедия");
    }

    @Test
    public void testFindById() {
        Optional<Genre> genreOptional = genreStorage.findById(1);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия"));
    }

}