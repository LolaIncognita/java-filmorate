package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;

@Data
@Builder
public class Mpa {
    private int id;
    @Max(10)
    private String name;
}
