package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film getFilmById(String id) {
        Long filmId = Long.parseLong(id);
        if (!(filmStorage.getFilms().containsKey(filmId))) {
            throw new NullPointerException(format("Фильма с id %s нет в системе.", id));
        }
        return filmStorage.getFilmById(filmId);
    }

    public Film createFilm(Film film) {
        filmValidation(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        filmValidation(film);
        return filmStorage.updateFilm(film);
    }

    public Film addLikes(String filmId, String userId) {
        Long idFilm = Long.parseLong((filmId));
        Long idUser = Long.parseLong(userId);
        if (!filmStorage.getFilms().containsKey(idFilm) || (!(userStorage.getUsers().containsKey(idUser)))) {
            throw new NullPointerException(format("Фильма с id %s или пользователя с id %s нет в базе", idFilm, idUser));
        } else {
            filmStorage.getFilmById(idFilm).getLikesUsersId().add(idUser);
        }
        return filmStorage.getFilms().get(idFilm);
    }

    public Film deleteLikes(String filmId, String userId) {
        Long idFilm = Long.parseLong((filmId));
        Long idUser = Long.parseLong(userId);
        if (!filmStorage.getFilms().containsKey(idFilm)) {
            throw new NullPointerException(format("Фильма с id %s нет в системе.", idFilm));
        } else if ((!(userStorage.getUsers().containsKey(idUser)))) {
            throw new NullPointerException(format("Пользователя с id %s нет в системе.", idFilm, idUser));
        } else {
            filmStorage.getFilms().get(idFilm).getLikesUsersId().remove(idUser);
        }
        return filmStorage.getFilms().get(idFilm);
    }

    public List<Film> popular(int count) {
        if (count <= 0) {
            log.error("popular: Запрошенное количество фильмов меньше или равно нулю.");
            throw new ValidationException("Запрошенное количество фильмов меньше или равно нулю: count = " + count);
        }
        return filmStorage.getFilms().values().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikesUsersId().size(), o1.getLikesUsersId().size()))
                .limit(count)
                .collect(Collectors.toList());
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
