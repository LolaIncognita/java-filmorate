package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private static long countOfUsers = 0;

    @Override
    public User createUser(User user) {
        countOfUsers++;
        user.setId(countOfUsers);
        users.put(user.getId(), user);
        log.debug("Добавление пользователя {} прошло успешно).",user.getLogin());
        return user;
    }

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Обновление пользователя {} прошло успешно.", user.getLogin());
        } else {
            log.warn("Обновление пользователя (ошибка: пользователя {} нет в системе).", user.getLogin());
            throw new NullPointerException("Пользователя нет в системе.");
        }

        return user;
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public User deleteUserById(String id) {
        Long userId = Long.parseLong(id);
        if (!users.containsKey(userId)) {
            log.error("Нельзя удалить: пользователя с id {} нет в базе данных", userId);
            throw new NullPointerException("Пользователя нет в базе данных.");
        } else {
            log.info("Удален пользователь с id: {}", userId);
            return users.remove(userId);
        }
    }
}
