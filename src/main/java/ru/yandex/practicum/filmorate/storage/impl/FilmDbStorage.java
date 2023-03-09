package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_FILM_BY_ID = "SELECT * FROM films WHERE film_id = ?";
    private static final String SELECT_LIKES_BY_FILM_ID = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String SELECT_GENRES_BY_FILM_ID = "SELECT g.genre_id genre_id, g.name name FROM genres g " +
            "LEFT JOIN film_genre fg ON fg.genre_id = g.genre_id " +
            "WHERE fg.film_id = ?";
    private static final String SELECT_MPA_BY_FILM_ID = "SELECT m.mpa_id mpa_id, m.name name FROM mpa m " +
            "LEFT JOIN films f ON f.mpa_id = m.mpa_id " +
            "WHERE f.film_id = ?";
    private static final String UPDATE_FILM_BY_ID = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
            "WHERE film_id = ?";
    private static final String DELETE_GENRES_BY_FILM_ID = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String INSERT_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_LAST_FILM_ID = "SELECT MAX(film_id) last_id FROM films";
    private static final String DELETE_FILM_BY_ID = "DELETE FROM films WHERE film_id = ?";
    private static final String SELECT_ALL_FILMS = "SELECT * FROM films";
    private static final String SELECT_LIKES_BY_FILM_USER_ID = "SELECT like_id likes_count FROM likes " +
            "WHERE film_id = ? AND user_id = ?";
    private static final String INSERT_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String SELECT_POPULAR_FILMS = "SELECT f.* FROM films f LEFT JOIN likes l ON f.film_id = l.film_id " +
            "GROUP BY f.film_id ORDER BY COUNT(l.USER_ID) DESC " +
            "LIMIT ?";

    @Override
    public Optional<Film> findById(Long id) {
        List<Film> films = jdbcTemplate.query(SELECT_FILM_BY_ID, (rs, rowNum) -> makeFilm(rs), id);

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

        List<Long> likes = jdbcTemplate.query(SELECT_LIKES_BY_FILM_ID,
                (rowSet, rowNum) -> rowSet.getLong("user_id"),
                id);

        List<Genre> genres = jdbcTemplate.query(SELECT_GENRES_BY_FILM_ID, (rowSet, rowNum) -> makeGenre(rowSet), id);

        List<Mpa> mpa = jdbcTemplate.query(SELECT_MPA_BY_FILM_ID, (rowSet, rowNum) -> makeMpa(rowSet), id);

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
        SqlRowSet filmsRow = jdbcTemplate.queryForRowSet(SELECT_FILM_BY_ID, film.getId());
        if (!filmsRow.next()) {
            log.warn(String.format("Фильм с id = %d не существует", film.getId()));
            throw new NotFoundException(String.format("Фильм с id = %d не существует", film.getId()));
        }

        jdbcTemplate.update(UPDATE_FILM_BY_ID, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        updateGenres(film);

        return film;
    }

    private void updateGenres(Film film) {
        jdbcTemplate.update(DELETE_GENRES_BY_FILM_ID, film.getId());
        if (!Objects.isNull(film.getGenres())) {
            insertGenres(film);
        }
    }

    private void insertGenres(Film film) {
        jdbcTemplate.batchUpdate(INSERT_GENRE,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getId());
                        ps.setInt(2, new ArrayList<>(film.getGenres()).get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });
    }

    @Override
    public Film add(Film film) {
        jdbcTemplate.update(INSERT_FILM, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

        SqlRowSet lastIdRow = jdbcTemplate.queryForRowSet(SELECT_LAST_FILM_ID);
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
        jdbcTemplate.update(DELETE_FILM_BY_ID, id);

        return film.orElse(null);
    }

    @Override
    public Collection<Film> findAll() {
        return jdbcTemplate.query(SELECT_ALL_FILMS, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public void like(Film film, Long userId) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(SELECT_LIKES_BY_FILM_USER_ID, film.getId(), userId);
        if (!sqlRowSet.next()) {
            jdbcTemplate.update(INSERT_LIKE, film.getId(), userId);
        }
    }

    @Override
    public void unlike(Film film, Long userId) {
        jdbcTemplate.update(DELETE_LIKE, film.getId(), userId);
    }

    @Override
    public Collection<Film> findPopular(Integer count) {
        return jdbcTemplate.query(SELECT_POPULAR_FILMS, (rs, rowNum) -> makeFilm(rs), count);
    }
}
