package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    Collection<User> findAllUsers();

    Map<Long, User> getUsers();

    User deleteUserById(String id);
}
