package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.MpaDbStorage;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/testdata.sql"})
class MpaDbStorageTest {

    private final MpaDbStorage mpaStorage;

    @Test
    public void testFindAll() {
        ArrayList<Mpa> mpaCollection = (ArrayList<Mpa>) mpaStorage.findAll();

        assertEquals(5, mpaCollection.size());
        assertEquals(mpaCollection.get(0).getName(), "G");
    }

    @Test
    public void testFindById() {
        Optional<Mpa> mpaOptional = mpaStorage.findById(3);

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "PG-13"));
    }

}