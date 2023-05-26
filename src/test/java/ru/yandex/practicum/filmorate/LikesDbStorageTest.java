package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LikesDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikesDbStorage likesDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Test
    void shouldAddLikeToFilm() {
        Film filmTest = createFirstFilm();
        filmDbStorage.createFilm(filmTest);
        User user = createFirstUser();
        userDbStorage.createUser(user);
        likesDbStorage.addLikeToFilm(1, 1);

        List<Long> likes = likesDbStorage.getFilmLikes(1L);

        assertEquals(1, likes.size(), "Списки лайков не совпадают");
    }

    @Test
    void shouldDeleteLikesFromFilm() {
        Film filmTest = createFirstFilm();
        filmDbStorage.createFilm(filmTest);
        User user = createFirstUser();
        userDbStorage.createUser(user);
        likesDbStorage.addLikeToFilm(1, 1);
        likesDbStorage.deleteLikeFromFilm(1, 1);

        List<Long> likes = likesDbStorage.getFilmLikes(1);
        assertEquals(0, likes.size(), "Списки лайков не совпадают");
    }

    @Test
    void shouldGetPopularFilm() {
        Film firstFilm = createFirstFilm();
        Film secondFilm = createSecondFilm();
        Film testFilm1 = filmDbStorage.createFilm(firstFilm);
        Film testFilm2 = filmDbStorage.createFilm(secondFilm);
        User user = createFirstUser();
        User user1 = createSecondUser();
        userDbStorage.createUser(user);
        userDbStorage.createUser(user1);
        likesDbStorage.addLikeToFilm(2, 1);
        likesDbStorage.addLikeToFilm(2, 2);

        List<Film> popularFilms = likesDbStorage.getPopularFilms(10);

        List<Film> testList = new ArrayList<>();
        testList.add(testFilm2);
        testList.add(testFilm1);

        assertEquals(testList, popularFilms, "Списки не равны");
    }

    @Test
    void shouldGetPopularFilmsWithoutFilms() {
        List<Film> popularFilms = likesDbStorage.getPopularFilms(10);
        assertThat(popularFilms)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    private User createFirstUser() {
        return User.builder().id(1).email("email@mail.ru").login("login").name("name")
                .birthday(LocalDate.of(1984, 2, 6)).build();
    }

    private User createSecondUser() {
        return User.builder().id(2).email("updated@mail.ru").login("loginUpdated").name("UpdatedName")
                .birthday(LocalDate.of(2001, 5, 5)).build();
    }

    private Film createFirstFilm() {
        Mpa mpa = mpaDbStorage.getMpaById(1);
        return Film.builder().id(1).name("FirstName").description("description")
                .releaseDate(LocalDate.of(2020, 10, 10)).duration(180)
                .mpa(mpa).build();
    }

    private Film createSecondFilm() {
        Mpa mpa = mpaDbStorage.getMpaById(2);
        return Film.builder().id(1).name("SecondName").description("AfterUpdate")
                .releaseDate(LocalDate.of(2023, 5, 1)).duration(130).mpa(mpa).build();
    }
}