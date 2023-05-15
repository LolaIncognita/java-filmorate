package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    public void addLike(Long userId) {
        likes.add(userId);
    }
}