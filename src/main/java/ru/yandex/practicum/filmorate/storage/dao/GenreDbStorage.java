package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM Genre ORDER BY genre_id";
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    public Genre getGenreById(int genreId) {
        String sql = "SELECT * FROM Genre WHERE genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new GenreMapper(), genreId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Жанр с id = {} не найден", genreId);
            throw new EntityNotFoundException(format("Жанр с id = %s не найден", genreId));
        }
    }
}