package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NullPointerForDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    private final LikesDbStorage likesDbStorage;
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film getFilmById(long filmId) {
        Film film;
        film = filmStorage.getFilmById(filmId);
        film.getGenres().addAll(filmStorage.getGenreByFilmId(filmId));
        film.getLikes().addAll(likesDbStorage.getFilmLikes(filmId));
        log.info("Получили фильм по id={}", filmId);
        return film;
    }

    public Film createFilm(Film film) {
        filmValidation(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        filmValidation(film);
        return filmStorage.updateFilm(film);
    }

    public void addLikes(long filmId, long userId) {
        filmValidation(filmStorage.getFilmById(filmId));
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        likesDbStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLikes(long filmId, long userId) {
        filmValidation(filmStorage.getFilmById(filmId));
        filmValidation(filmStorage.getFilmById(userId));
        log.info("Пользователь id = {} удалил лайк у фильма id = {}", userId, filmId);
        likesDbStorage.deleteLikeFromFilm(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            log.error("popular: Запрошенное количество фильмов меньше или равно нулю.");
            throw new ValidationException("Запрошенное количество фильмов меньше или равно нулю: count = " + count);
        }
        return likesDbStorage.getPopularFilms(count);
    }

    private void filmValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Добавление фильма. Ошибка валидации (отсутствует наименование фильма).");
            throw new ValidationException("В переданных данных отсутствует наименование фильма.");
        }
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("Добавление фильма. Ошибка валидации (описание фильма првышает максимальное количество знаков).");
            throw new ValidationException("Описание фильма превышает максимальное количество знаков (" +
                    MAX_DESCRIPTION_LENGTH + ").");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Добавление фильма. Ошибка валидации (дата релиза ранее минимальной даты).");
            throw new ValidationException("Дата релиза ранее минимальной даты (" +
                    MIN_RELEASE_DATE + ").");
        }
        if (film.getDuration() <= 0) {
            log.warn("Добавление фильма. Ошибка валидации (продолжительность фильма 0 или менее.).");
            throw new ValidationException("Продолжительность фильма должна быть более 0.");
        }
    }
}