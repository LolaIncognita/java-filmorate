package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NullPointerForDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.String.format;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User getUserById(long userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NullPointerForDataException(format("Пользователя с id %s нет в системе.", userId));
        }
        return userStorage.getUsers().get(userId);
    }

    public User createUser(User user) {
        userValidation(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        userValidation(user);
        return userStorage.updateUser(user);
    }

    public User addFriend(long userId, long friendId) {
        if (!(userStorage.getUsers().containsKey(userId)) || !(userStorage.getUsers().containsKey(friendId))) {
            throw new NullPointerForDataException(format("Пользователя с id %s или %s нет в системе.", userId, friendId));
        } else {
            userStorage.getUsers().get(userId).getFriendsId().add(friendId);
            userStorage.getUsers().get(friendId).getFriendsId().add(userId);
        }
        return userStorage.getUsers().get(userId);
    }

    public User deleteFriend(long userId, long friendId) {
        if (!userStorage.getUsers().containsKey(userId) || !userStorage.getUsers().containsKey(friendId)) {
            throw new NullPointerForDataException(format("deleteFriend. Пользователя с id %s или %s нет в системе.", userId, friendId));
        } else {
            userStorage.getUsers().get(userId).getFriendsId().remove(friendId);
            userStorage.getUsers().get(friendId).getFriendsId().remove(userId);
        }
        return userStorage.getUsers().get(userId);
    }

    public Collection<User> findFriendsById(long userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NullPointerForDataException(format("findFriends. Пользователя с id %s нет в системе.", userId));
        }
        Collection<User> usersFriends = new ArrayList<>();
        for (Long friend : userStorage.getUsers().get(userId).getFriendsId()) {
            usersFriends.add(userStorage.getUsers().get(friend));
        }
        return usersFriends;
    }

    public Collection<User> findMutualFriends(long userId, long friendId) {
        if (!userStorage.getUsers().containsKey(userId) || !userStorage.getUsers().containsKey(friendId)) {
            throw new NullPointerForDataException(format("Пользователя с id %s или %s нет в системе.", userId, friendId));
        }
        Collection<User> mutualFriends = new ArrayList<>();
        for (Long friend : userStorage.getUsers().get(userId).getFriendsId()) {
            if (userStorage.getUsers().get(friendId).getFriendsId().contains(friend)) {
                mutualFriends.add(userStorage.getUsers().get(friend));
            }
        }
        return mutualFriends;
    }

    private void userValidation(User user) {
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
    }
}