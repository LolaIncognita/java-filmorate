package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    public Collection<User> findAllUsers() {
        List<User> allUsers = userStorage.findAllUsers();
        for (User user : allUsers) {
            Set<Long> usersFriends = user.getFriendsId();
            usersFriends.addAll(friendshipDbStorage.getAllFriendsById(user.getId()).stream().map(User::getId)
                    .collect(Collectors.toSet()));
        }
        return allUsers;
    }

    public User getUserById(long userId) {
        User user = userStorage.getUserById(userId);
        Set<Long> usersFriends = user.getFriendsId();
        usersFriends.addAll(friendshipDbStorage.getAllFriendsById(user.getId()).stream().map(User::getId)
                .collect(Collectors.toSet()));
        return user;
    }

    public User createUser(User user) {
        userValidation(user);
        Set<Long> usersFriends = user.getFriendsId();
        for (Long friendId : usersFriends) {
            if (!userStorage.findAllUsers().contains(userStorage.getUserById(friendId))) {
                log.error("Пользователя с id = {} еще не существует", friendId);
                usersFriends.remove(friendId);
                throw new EntityNotFoundException(format("Пользователя с id = %s еще не существует", friendId));
            }
        }
        User createdUser = userStorage.createUser(user);
        Set<Long> friends = user.getFriendsId();
        for (Long friendId : friends) {
            addFriend(createdUser.getId(), friendId);
        }
        log.info("Добавили пользователя: {}", createdUser);
        return createdUser;
    }

    public User updateUser(User user) {
        userValidation(user);
        userStorage.getUserById(user.getId());
        Set<Long> userFriends = user.getFriendsId();
        userFriends.forEach(friendsId -> addFriend(user.getId(), friendsId));
        log.info("Обновили пользователя с id = {}", user.getId());
        return userStorage.updateUser(user);
    }

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.error("Попытка добавить себя в друзья");
            throw new ValidationException("Приложением не предусмотрено добавления себя в друзья.");
        }
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Long> usersFriends = user.getFriendsId();
        Set<Long> friendsFriends = friend.getFriendsId();
        boolean isUserHasFriend = usersFriends.contains(friendId);
        boolean isFriendHasUser = friendsFriends.contains(userId);
        if (!isUserHasFriend && !isFriendHasUser) {
            friendshipDbStorage.addFriend(userId, friendId);
            usersFriends.add(friendId);
            log.info("Пользователь id = {} добавил в друзья пользователя id = {}", userId, friendId);
        } else if (!isUserHasFriend) {
            friendshipDbStorage.addFriend(userId, friendId);
            friendshipDbStorage.updateFriendship(userId, friendId, true);
            friendshipDbStorage.updateFriendship(friendId, userId, true);
            log.info("Пользователь id = {} подтвердил дружбу с пользователем id = {}", userId, friendId);
            usersFriends.add(friendId);
        } else {
            log.info("Пользователь id = {} уже в друзьях у пользователя id = {}", friendId, userId);
            throw new ValidationException(format("Пользователь id = %s уже в друзьях у пользователя id = %s",
                    friendId, userId));
        }
    }

    where user_id in (?) и передать туда мапу


    public void deleteFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Long> usersFriends = user.getFriendsId();
        Set<Long> friendsFriends = friend.getFriendsId();
        if (!usersFriends.contains(friendId)) {
            log.error("Пользователь id = {} не в друзьях у пользователя id = {}", friendId, userId);
            throw new ValidationException(format("Пользователь id = %s не в друзьях у пользователя id = %s",
                    friendId, userId));
        } else if (!friendsFriends.contains(userId)) {
            friendshipDbStorage.deleteFriend(userId, friendId);
            log.info("Пользователь id = {} удалил из друзей пользователя id = {}", userId, friendId);
        } else {
            friendshipDbStorage.deleteFriend(userId, friendId);
            friendshipDbStorage.updateFriendship(friendId, userId, false);
            log.info("Пользователь id = {} удалил из друзей пользователя id = {}, статус дружбы обновлен",
                    userId, friendId);
        }
    }

    public Collection<User> findFriendsById(long userId) {
        return friendshipDbStorage.getAllFriendsById(userId);
    }

    public Collection<User> findMutualFriends(long userId, long friendId) {
        return friendshipDbStorage.getCommonFriends(userId, friendId);
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