package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL_GENRES = "SELECT * FROM genres";
    private static final String SELECT_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";

    @Override
    public Collection<Genre> findAll() {
        return jdbcTemplate.query(SELECT_ALL_GENRES, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(SELECT_GENRE_BY_ID, id);
        if (!sqlRowSet.next()) {
            return Optional.empty();
        }

        Genre genre = new Genre(
                sqlRowSet.getInt("genre_id"),
                sqlRowSet.getString("name")
        );
        return Optional.of(genre);
    }
}
