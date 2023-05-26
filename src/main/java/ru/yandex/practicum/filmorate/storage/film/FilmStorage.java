package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film createFilm(Film film);

    Collection<Film> findAllFilms();

    Film updateFilm(Film film);

    Film getFilmById(long filmId);

    void deleateFilmById(long id);

    List<Genre> getGenreByFilmId(long filmId);
}
