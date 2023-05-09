package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NullPointerForDataException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private static int countOfFilms = 0;

    @Override
    public Film createFilm(Film film) {
        countOfFilms++;
        film.setId(countOfFilms);
        films.put(film.getId(), film);
        log.debug("Добавление фильма (успешно). Текущее количество фильмов: {}", films.size());
        return film;
    }

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Обновление фильма. Текущее количество фильмов: {}", films.size());
        } else {
            log.warn("Обновление фильма (ошибка: фильма нет в системе).");
            throw new NullPointerForDataException("Фильма нет в системе");
        }

        return film;
    }

    @Override
    public Film getFilmById(long filmId) {
        return films.get(filmId);
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Film deleateFilmById(String id) {
        Long filmId = Long.parseLong(id);
        if (!films.containsKey(filmId)) {
            log.error("Нельзя удалить: пользователя с id {} нет в базе данных", filmId);
            throw new NullPointerForDataException("Пользователя нет в базе данных.");
        } else {
            log.info("Удален пользователь с id: {}", filmId);
            return films.remove(filmId);
        }
    }
}