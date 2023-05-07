package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private String login;
    private String name;
    private long id;
    private String email;
    private LocalDate birthday;
    private Set<Long> friendsId = new HashSet<>();

    public User(long id, String email, String login, String name, LocalDate birthday) {
        this.login = login;
        this.name = name;
        this.id = id;
        this.email = email;
        this.birthday = birthday;
    }
}