package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

@Data
@Builder
public class Film {

    private long id;
    private String name;
    private LocalDate releaseDate;
    private String description;
    private int duration;
    private final Set<Long> likes = new HashSet<>();
    private Mpa mpa;
    private final Set<Genre> genres = new HashSet<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void removeGenre (Genre genre) throws EntityNotFoundException {
        if(genres.contains(genre)) {
            genres.remove(genre);
        } else {
            throw new EntityNotFoundException(format("Жанр %s не может быть удалён, его нет в списке жанров фильма %s.", genre, this.name));
        }
    }

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void removeLike(Long userId) throws EntityNotFoundException {
        if(likes.contains(userId)) {
            likes.remove(userId);
        } else {
            throw new EntityNotFoundException(format("Лайк пользователя с id = %s не может быть удалён, в базе отсутствует информация о поставленном ранее лайке.", userId));
        }
    }
}