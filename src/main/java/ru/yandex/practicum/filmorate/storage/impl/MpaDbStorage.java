package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL_MPA = "SELECT * FROM mpa";
    private static final String SELECT_MPA_BY_ID = "SELECT * FROM mpa WHERE mpa_id = ?";

    @Override
    public Collection<Mpa> findAll() {
        return jdbcTemplate.query(SELECT_ALL_MPA, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("mpa_id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }

    @Override
    public Optional<Mpa> findById(int id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(SELECT_MPA_BY_ID, id);
        if (!sqlRowSet.next()) {
            return Optional.empty();
        }

        Mpa mpa = new Mpa(
                sqlRowSet.getInt("mpa_id"),
                sqlRowSet.getString("name")
        );

        return Optional.of(mpa);
    }
}
