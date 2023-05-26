package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Test
    void shouldCreateFilmWithId() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void shouldCreateFilmWithName() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "TestName")
                );
    }

    @Test
    void shouldCreateFilmWithDescription() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description",
                                "description")
                );
    }

    @Test
    void shouldCreateFilmWithReleaseDate() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("releaseDate",
                                LocalDate.of(2020, 10, 10))
                );
    }

    @Test
    void shouldCreateFilmWithDuration() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("duration", 180)
                );
    }

    @Test
    void shouldCreateFilmWithMpa() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Mpa mpa = mpaDbStorage.getMpaById(1);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film.getMpa()).isEqualTo(mpa)
                );
    }

    @Test
    void shouldCreateFilmWithGenre() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Set<Genre> genres = new HashSet<>();
        genres.add(genreDbStorage.getGenreById(1));

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film.getGenres()).isEqualTo(genres)
                );
    }

    @Test
    void shouldUpdateFilmById() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = createFilmForUpdate();

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void shouldUpdateFilmWithName() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = createFilmForUpdate();

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "AfterUpdateName")
                );
    }

    @Test
    void shouldUpdateFilmWithDescription() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = createFilmForUpdate();

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description",
                                "afterUpdateDescription")
                );
    }

    @Test
    void shouldUpdateFilmWithDuration() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = createFilmForUpdate();

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("duration", 130)
                );
    }

    @Test
    void shouldUpdateFilmWithMpa() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = createFilmForUpdate();
        Mpa mpa = mpaDbStorage.getMpaById(2);

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film.getMpa()).isEqualTo(mpa)
                );
    }

    @Test
    void shouldUpdateFilmWithGenre() {
        Film testFilm = createTestFilm();
        filmStorage.createFilm(testFilm);
        Film filmForUpdate = createFilmForUpdate();
        Set<Genre> testGenres = new HashSet<>();
        testGenres.add(genreDbStorage.getGenreById(2));

        filmStorage.updateFilm(filmForUpdate);
        Optional<Film> filmOptionalUpdated = Optional.ofNullable(filmStorage.getFilmById(1));

        assertThat(filmOptionalUpdated)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film.getGenres()).isEqualTo(testGenres)
                );
    }

    @Test
    void shouldGetAllFilms() {
        Film film = createTestFilm();
        Film film1 = createFilmForUpdate();
        film1.setId(2);
        filmStorage.createFilm(film);
        filmStorage.createFilm(film1);

        List<Film> allFilms = filmStorage.findAllFilms();

        assertEquals(2, allFilms.size());
    }

    private Film createTestFilm() {
        Mpa mpa = mpaDbStorage.getMpaById(1);
        Film testFilm = Film.builder().id(1).name("TestName").description("description")
                .releaseDate(LocalDate.of(2020, 10, 10)).duration(180)
                .mpa(mpa).build();
        Set<Genre> genres = testFilm.getGenres();
        genres.add(genreDbStorage.getGenreById(1));
        return testFilm;
    }

    private Film createFilmForUpdate() {
        Mpa mpa = mpaDbStorage.getMpaById(2);
        Film filmForUpdate = Film.builder().id(1).name("AfterUpdateName").description("afterUpdateDescription")
                .releaseDate(LocalDate.of(2023, 5, 1)).duration(130).mpa(mpa).build();
        Set<Genre> testGenres = filmForUpdate.getGenres();
        testGenres.add(genreDbStorage.getGenreById(2));
        return filmForUpdate;
    }
}