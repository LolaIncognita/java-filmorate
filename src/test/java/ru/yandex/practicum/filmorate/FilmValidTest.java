package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest
public class FilmValidTest {
    public FilmController filmController = new FilmController();

    @Test
    void simpleFilmAddTest() {
        Film filmForTest = new Film(1, "filmName", "filmDescription",
                LocalDate.of(2000,12,28), 120);
        Film film = filmController.create(filmForTest);
        Assertions.assertEquals(film, filmForTest);
    }

    //название фильма не может быть пустым
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

    //максимальная длина описания фильма — 200 символов;
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

    //дата релиза фильма — не раньше 28 декабря 1895 года;
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

    //продолжительность фильма должна быть положительной.
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
