package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private static int countOfUsers = 0;

    //создание пользователя
    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Добавление пользователя. Ошибка валидации (отсутствует адрес электронной почты).");
            throw new ValidationException("В переданных данных отсутствует адрес электронной почты.");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Добавление пользователя. Ошибка валидации (отсутствует @ в адресе электронной почты).");
            throw new ValidationException("В адресе электронной почты отсутствует символ @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Добавление пользователя. Ошибка валидации (логин не может быть пустым или содержать пробелы).");
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Добавление пользователя. Ошибка валидации (дата рождения не может быть в будущем).");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.warn("Добавление пользователя. Пустое имя пользователя, установлен логин в качестве имени.");
            user.setName(user.getLogin());
        }
        countOfUsers++;
        user.setId(countOfUsers);
        users.put(user.getId(), user);
        log.debug("Добавление пользователя (успешно). Текущее количество пользователей: {}", users.size());

        return user;
    }

    //обновление пользователя
    @PutMapping
    public User put(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Обновление пользователя (успешно). Текущее количество пользователей: {}", users.size());
        } else {
            log.warn("Обновление пользователя (ошибка: пользователя нет в системе).");
            throw new ValidationException("Пользователя нет в системе");
        }

        return user;
    }

    //получение списка всех пользователей
    @GetMapping
    public Collection<User> findAll () {
        return users.values();
    }
}