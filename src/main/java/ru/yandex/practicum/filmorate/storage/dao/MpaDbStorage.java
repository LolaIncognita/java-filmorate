package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NullPointerForDataException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM Mpa ORDER BY mpa_id";
        return jdbcTemplate.query(sql, new MpaMapper());
    }

    public Mpa getMpaById(int mpaId) {
        String sql = "SELECT * FROM Mpa WHERE mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new MpaMapper(), mpaId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Mpa с id={} не найден", mpaId);
            throw new NullPointerForDataException(format("Mpa с id = %s не найден", mpaId));
        }
    }
}