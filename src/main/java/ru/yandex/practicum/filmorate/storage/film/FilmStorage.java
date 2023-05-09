package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Film createFilm(Film film);

    Collection<Film> findAllFilms();

    Film updateFilm(Film film);

    Film getFilmById(long filmId);

    Map<Long, Film> getFilms();

    Film deleateFilmById(String filmId);
}
