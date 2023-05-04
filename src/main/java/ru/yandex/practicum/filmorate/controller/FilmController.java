package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static int countOfFilms = 0;

    //добавление фильма
    @PostMapping
    public Film create(@RequestBody Film filmFromRequest) {
        Film film = filmFromRequest;
        if (filmFromRequest.getName() == null || filmFromRequest.getName().isBlank()) {
            log.warn("Добавление фильма. Ошибка валидации (отсутствует наименование фильма).");
            throw new ValidationException("В переданных данных отсутствует наименование фильма.");
        } else if (filmFromRequest.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("Добавление фильма. Ошибка валидации (описание фильма првышает максимальное количество знаков).");
            throw new ValidationException("Описание фильма превышает максимальное количество знаков (" +
                    MAX_DESCRIPTION_LENGTH + ").");
        } else if (filmFromRequest.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Добавление фильма. Ошибка валидации (дата релиза ранее минимальной даты).");
            throw new ValidationException("Дата релиза ранее минимальной даты (" +
                    MIN_RELEASE_DATE + ").");
        } else if (filmFromRequest.getDuration() <= 0) {
            log.warn("Добавление фильма. Ошибка валидации (продолжительность фильма 0 или менее.).");
            throw new ValidationException("Продолжительность фильма должна быть более 0.");
        } else {
            countOfFilms++;
            film.setId(countOfFilms);
            films.put(film.getId(), film);
            log.debug("Добавление фильма (успешно). Текущее количество фильмов: {}", films.size());
        }
        return filmFromRequest;
    }

    //обновление фильма
    @PutMapping
    public Film put(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Обновление фильма. Текущее количество фильмов: {}", films.size());
        } else {
            log.warn("Обновление фильма (ошибка: фильма нет в системе).");
            throw new ValidationException("Фильма нет в системе");
        }
        return film;
    }

    //получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }
}