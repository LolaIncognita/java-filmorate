package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {

    private String login;
    private String name;
    private long id;
    private String email;
    private LocalDate birthday;
    private final Set<Long> friendsId = new HashSet<>();
}