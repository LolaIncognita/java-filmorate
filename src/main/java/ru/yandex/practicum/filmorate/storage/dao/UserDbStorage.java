package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NullPointerForDataException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO Users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.debug("Добавление пользователя {} прошло успешно).",user.getLogin());
        return user;
    }

    @Override
    public User updateUser(User user) {
        try {
            jdbcTemplate.update("UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?",
                    user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId());
            log.debug("Обновление пользователя {} прошло успешно.", user.getLogin());
        } catch (EmptyResultDataAccessException e) {
            log.warn("Обновление пользователя (ошибка: пользователя {} нет в системе).", user.getLogin());
            throw new NullPointerForDataException(format("Пользователя с id= %s нет в базе.", user.getId()));
        }
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return jdbcTemplate.query("SELECT * FROM Users", new UserMapper());
    }

    @Override
    public User getUserById(long id) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        try {
            log.info("Нашли пользователя с id = {}", id);
            return jdbcTemplate.queryForObject(sql, new UserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NullPointerException(format("Пользователя с id= %s нет в базе", id));
        }
    }

    @Override
    public void deleteUserById(long id) {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try {
            jdbcTemplate.update(sql, id);
            log.info("Удален пользователь с id: {}", id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Нельзя удалить: пользователя с id {} нет в базе данных.", id);
            throw new NullPointerException(format("Пользователя с id= %s нет в базе.", id));
        }
    }
}
