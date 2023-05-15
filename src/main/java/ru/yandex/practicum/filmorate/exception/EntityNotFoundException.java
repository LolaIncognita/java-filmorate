package ru.yandex.practicum.filmorate.exception;

public class EntityNotFoundException extends NullPointerException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
