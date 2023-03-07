package ru.yandex.practicum.filmorate.storage.impl.dao;

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

    @Override
    public Collection<Genre> findAll() {
        String selectSql = "SELECT * FROM genres";
        return jdbcTemplate.query(selectSql, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        String selectSql = "SELECT * FROM genres WHERE genre_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(selectSql, id);
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
