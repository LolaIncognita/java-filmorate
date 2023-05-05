package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private String login;
    private String name;
    private int id;
    private String email;
    private LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.login = login;
        this.name = name;
        this.id = id;
        this.email = email;
        this.birthday = birthday;
    }
}