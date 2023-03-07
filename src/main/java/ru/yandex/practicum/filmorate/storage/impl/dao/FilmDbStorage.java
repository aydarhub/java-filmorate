package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Film> findById(Long id) {
        String filmSql = "SELECT * FROM films WHERE film_id = ?";
        List<Film> films = jdbcTemplate.query(filmSql, (rs, rowNum) -> makeFilm(rs), id);

        if (films.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(films.get(0));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");

        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(likesSql,
                (rowSet, rowNum) -> rowSet.getLong("user_id"),
                id);

        String genresSql = "SELECT g.genre_id genre_id, g.name name FROM genres g " +
                "LEFT JOIN film_genre fg ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genresSql, (rowSet, rowNum) -> makeGenre(rowSet), id);

        String mpaSql = "SELECT m.mpa_id mpa_id, m.name name FROM mpa m " +
                "LEFT JOIN films f ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        List<Mpa> mpa = jdbcTemplate.query(mpaSql, (rowSet, rowNum) -> makeMpa(rowSet), id);

        return new Film(id, name, description, releaseDate, duration, likes, new LinkedHashSet<>(genres), mpa.get(0));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("mpa_id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }


    @Override
    public Film update(Film film) {
        String filmsSql = "SELECT film_id FROM films WHERE film_id = ?";
        SqlRowSet filmsRow = jdbcTemplate.queryForRowSet(filmsSql, film.getId());
        if (!filmsRow.next()) {
            log.warn(String.format("Фильм с id = %d не существует", film.getId()));
            throw new NotFoundException(String.format("Фильм с id = %d не существует", film.getId()));
        }

        String updateQuery = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(updateQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        updateGenres(film);

        return film;
    }

    private void updateGenres(Film film) {
        String deleteGenresQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresQuery, film.getId());
        if (film.getGenres() != null) {
            insertGenres(film);
        }
    }

    private void insertGenres(Film film) {
        String insertGenreQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(insertGenreQuery, film.getId(), genre.getId());
        }
    }

    @Override
    public Film add(Film film) {
        String insertQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

        String lastIdQuery = "SELECT MAX(film_id) last_id FROM films";
        SqlRowSet lastIdRow = jdbcTemplate.queryForRowSet(lastIdQuery);
        if (lastIdRow.next()) {
            film.setId(lastIdRow.getLong("last_id"));
        }

        if (film.getGenres() != null) {
            insertGenres(film);
        }

        return film;
    }

    @Override
    public Film delete(Long id) {
        Optional<Film> film = findById(id);
        String deleteQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(deleteQuery, id);

        return film.orElse(null);
    }

    @Override
    public Collection<Film> findAll() {
        String filmsSql = "SELECT * FROM films";

        return jdbcTemplate.query(filmsSql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public void like(Film film, Long userId) {
        String likeExistsQuery = "SELECT like_id likes_count FROM likes " +
                "WHERE film_id = ? AND user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(likeExistsQuery, film.getId(), userId);
        if (!sqlRowSet.next()) {
            String likeQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(likeQuery, film.getId(), userId);
        }
    }

    @Override
    public void unlike(Film film, Long userId) {
        String unlikeQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(unlikeQuery, film.getId(), userId);
    }

    @Override
    public Collection<Film> findPopular(Integer count) {
        String filmsSql = "SELECT f.* FROM films f LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id ORDER BY COUNT(l.USER_ID) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(filmsSql, (rs, rowNum) -> makeFilm(rs), count);
    }
}
