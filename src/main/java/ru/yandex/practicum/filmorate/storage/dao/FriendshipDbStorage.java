package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addFriend(long userId, long friendId) {
        try {
            String sql = "INSERT INTO Friendship (user_id, friend_id, status) VALUES(?, ?, false)";
            jdbcTemplate.update(sql, userId, friendId);
        } catch (DataAccessException exception) {
            log.error("Пользователь с id = {} уже в друзьях у пользователя с id = {}", friendId, userId);
            throw new ValidationException(String.format("Пользователь с id = %s уже в друзьях у пользователя с id = %s",
                    friendId, userId));
        }
    }

    public void updateFriendship(long userId, long friendId, boolean status) {
        String sql = "UPDATE Friendship SET status = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, status, userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM Friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> getAllFriendsById(long userId) {
        String sql = "SELECT * FROM Users WHERE user_id IN (SELECT friend_id FROM Friendship WHERE user_id = ?)";
        try {
            log.info("Получили список друзей пользователя id={}", userId);
            return jdbcTemplate.query(sql, new UserMapper(), userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NullPointerException(format("Пользователя с id= %s нет в базе", userId));
        }
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        List<User> commonFriends = new ArrayList<>();
        List<User> friendsOfUser = getAllFriendsById(userId);
        List<User> friendsOfFriend = getAllFriendsById(friendId);
        for (User user : friendsOfUser) {
            if (friendsOfFriend.contains(user)) {
                commonFriends.add(user);
            }
        }
        log.info("Получили список общих друзей пользователей id={} и id={}", userId, friendId);
        return commonFriends;
    }
}
