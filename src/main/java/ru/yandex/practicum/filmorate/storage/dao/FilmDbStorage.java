package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.function.Function.identity;


@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO Film (name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        film.getGenres().forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));
        log.debug("Фильм {} сохранен в базу.", film.getName());
        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        String sql = "SELECT f.*, m.name FROM Film AS f JOIN Mpa AS m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper());
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final String sqlQuery = "select * from GENRE g, GENREFILM fg where fg.GENRE_ID = g.GENRE_ID AND fg.FILM_ID in " +
                "(" + inSql + ")";
        jdbcTemplate.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getLong("FILM_ID"));
            film.addGenre(makeGenre(rs));
        }, films.stream().map(Film::getId).toArray());
        final String sqlLikes = "SELECT * FROM Likes";
        jdbcTemplate.query(sqlLikes, (rs) -> {
            final Film film = filmById.get(rs.getLong("film_id"));
            film.addLike(rs.getLong("user_id"));
        });
        return films;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE Film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                " WHERE film_id = ?";
        int amountOperations = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (amountOperations > 0) {
            deleteAllGenresFromFilm(film.getId());
            Set<Genre> genres = film.getGenres();
            genres.forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));
            genres.clear();
            genres.addAll(getGenreByFilmId(film.getId()));
            log.debug("Обновление фильма {} прошло успешно", film.getId());
            return film;
        }
        log.warn("Обновление фильма (ошибка: фильма нет в системе).");
        throw new EntityNotFoundException(String.format("Фильм с id = %s не найден в базе.", film.getId()));
    }

    @Override
    public Film getFilmById(long filmId) {
        String sql = "SELECT f.*, m.name FROM Film AS f JOIN mpa AS m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, new FilmMapper(), filmId);
            List<Genre> genres = getGenreByFilmId(filmId);
            genres.forEach(genre -> film.addGenre(genre));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(format("Фильма с id= %s нет в базе", filmId));
        }
    }

    @Override
    public List<Genre> getGenreByFilmId(long filmId) {
        String sql = "SELECT g.* FROM GenreFilm AS gf JOIN Genre AS g ON gf.genre_id = g.genre_id WHERE " +
                "gf.film_id = ? ORDER BY g.genre_id";
        try {
            return jdbcTemplate.query(sql, new GenreMapper(), filmId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Фильма с id={} нет в базе", filmId);
            throw new EntityNotFoundException(format("Фильма с id= %s нет в базе", filmId));
        }
    }

    private void addGenreToFilm(long filmId, int genreId) {
        String sql = "INSERT INTO GenreFilm (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    private void deleteAllGenresFromFilm(long filmId) {
        String sql = "DELETE FROM GenreFilm WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public void deleateFilmById(long id) {
        String sql = "DELETE FROM Film WHERE film_id = ?";
        try {
            jdbcTemplate.update(sql, id);
            log.info("Удален пользователь с id: {}", id);
        } catch (EmptyResultDataAccessException e) {
            throw new NullPointerException(format("Фильма с id= %s нет в базе.", id));
        }
    }
}