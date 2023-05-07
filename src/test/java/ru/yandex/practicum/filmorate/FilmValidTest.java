package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

@SpringBootTest
public class FilmValidTest {
    public FilmController filmController = new FilmController(new FilmService(new InMemoryUserStorage(), new InMemoryFilmStorage()));

    @Test
    void simpleFilmAddTest() {
        Film filmForTest = new Film(1, "filmName", "filmDescription",
                LocalDate.of(2000,12,28), 120);
        Film film = filmController.create(filmForTest);
        Assertions.assertEquals(film, filmForTest);
    }

    @Test
    void addFilmWithId2Test() {
        Film filmForTest1 = new Film(1, "New film", "New film about friends",
                LocalDate.of(1999,04,30), 120);
        Film filmForTest2 = new Film(2, "New film", "New film about friends",
                LocalDate.of(1999,04,30), 120);

        Film film = filmController.create(filmForTest2);
        Assertions.assertEquals(film.getId(), filmForTest2.getId());
    }

    @Test
    void getFilmByIdTest() {
        Film filmForTest1 = new Film(1, "New film", "New film about friends",
                LocalDate.of(1999,04,30), 120);
        Film filmForTest2 = new Film(2, "New film", "New film about friends",
                LocalDate.of(1999,04,30), 120);
        Film film1 = filmController.create(filmForTest1);
        Film film2 = filmController.create(filmForTest2);
        Assertions.assertEquals(1, film1.getId());
    }

    @Test
    void addFilmWithoutNameTest() {
        Film filmForTest = new Film(1, "", "filmDescription",
                LocalDate.of(2000,12,28), 120);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Film film = filmController.create(filmForTest);
            }
        });
        Assertions.assertEquals("В переданных данных отсутствует наименование фильма.", exception.getMessage());
    }

    @Test
    void addFilmWithLongDescriptionTest() {
        Film filmForTest = new Film(1, "filmName", "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901",
                LocalDate.of(2000,12,28), 120);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Film film = filmController.create(filmForTest);
            }
        });
        Assertions.assertEquals("Описание фильма превышает максимальное количество знаков (200).", exception.getMessage());
    }

    @Test
    void addFilmWithOldReleaseTest() {
        Film filmForTest = new Film(1, "filmName", "filmDescription",
                LocalDate.of(1895,12,27), 120);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Film film = filmController.create(filmForTest);
            }
        });
        Assertions.assertEquals("Дата релиза ранее минимальной даты (" + LocalDate.of(1895,12,28) + ").", exception.getMessage());
    }

    @Test
    void addFilmWithNegativeDurationTest() {
        Film filmForTest = new Film(1, "filmName", "filmDescription",
                LocalDate.of(2000,12,28), -1);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Film film = filmController.create(filmForTest);
            }
        });
        Assertions.assertEquals("Продолжительность фильма должна быть более 0.", exception.getMessage());
    }

    @Test
    void addFilmWithZeroDurationTest() {
        Film filmForTest = new Film(1, "filmName", "filmDescription",
                LocalDate.of(2000,12,28), 0);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Film film = filmController.create(filmForTest);
            }
        });
        Assertions.assertEquals("Продолжительность фильма должна быть более 0.", exception.getMessage());
    }
}