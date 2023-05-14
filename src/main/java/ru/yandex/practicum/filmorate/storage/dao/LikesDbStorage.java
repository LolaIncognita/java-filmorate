package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikesDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public void addLikeToFilm(long filmId, long userId) {
        try {
            String sql = "INSERT INTO Likes (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, userId);
        } catch (DataAccessException exception) {
            log.error("Пользователь id = {} уже поставил лайк фильму id = {}", userId, filmId);
            throw new ValidationException(format("Пользователь id = %s уже поставил лайк фильму id = %s",
                    userId, filmId));
        }
    }

    public void deleteLikeFromFilm(long filmId, long userId) {
        String sql = "DELETE FROM Likes WHERE (film_id = ? AND user_id = ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Long> getFilmLikes(long filmId) {
        String sql = "SELECT user_id FROM Likes WHERE film_id = ?";
        try {
            return jdbcTemplate.query(sql, (rs, rowNun) -> rs.getLong("user_id"), filmId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Фильма с id={} нет в базе", filmId);
            throw new NullPointerException(format("Фильма с id= %s нет в базе", filmId));
        }
    }

    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM Film AS f JOIN Mpa AS m ON f.mpa_id = m.mpa_id" +
                " LEFT JOIN (SELECT film_id, COUNT(user_id) AS likes_count FROM Likes GROUP BY film_id ORDER BY " +
                "likes_count) AS popular on f.film_id = popular.film_id ORDER BY popular.likes_count DESC LIMIT ?";
        return jdbcTemplate.query(sql, new FilmMapper(), count);
    }
}