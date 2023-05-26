package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public List<Genre> getAllGenres() {
        log.info("Получен список жанров");
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenreById(int genreId) {
        return genreDbStorage.getGenreById(genreId);
    }
}