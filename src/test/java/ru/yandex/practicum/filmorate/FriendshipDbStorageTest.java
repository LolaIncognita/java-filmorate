package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FriendshipDbStorageTest {

    private final FriendshipDbStorage friendshipDbStorage;
    private final UserDbStorage userStorage;

    @Test
    void shouldAddFriend() {
        User user1 = createTestUser();
        User user2 = createSecondUser();
        userStorage.createUser(user1);
        userStorage.createUser(user2);
        friendshipDbStorage.addFriend(1, 2);

        List<User> friendsOfUser1 = friendshipDbStorage.getAllFriendsById(1);
        List<User> friendsOfUser2 = friendshipDbStorage.getAllFriendsById(2);

        assertEquals(1, friendsOfUser1.size(), "Списки друзей не совпадают");
        assertEquals(0, friendsOfUser2.size(), "Списки друзей не совпадают");
    }

    @Test
    void shouldDeleteFriend() {
        User user1 = createTestUser();
        User user2 = createSecondUser();
        userStorage.createUser(user1);
        userStorage.createUser(user2);
        friendshipDbStorage.addFriend(1, 2);
        friendshipDbStorage.deleteFriend(1, 2);

        List<User> friendsOfUser1 = friendshipDbStorage.getAllFriendsById(1);

        assertEquals(0, friendsOfUser1.size(), "Списки друзей не совпадают");
    }

    @Test
    void shouldGetCommonFriends() {
        User user1 = createTestUser();
        User user2 = createSecondUser();
        User user3 = createThirdUser();
        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.createUser(user3);
        friendshipDbStorage.addFriend(1, 3);
        friendshipDbStorage.addFriend(2, 3);

        List<User> commonFriends = friendshipDbStorage.getCommonFriends(1, 2);

        assertEquals(userStorage.getUserById(3), commonFriends.get(0), "Списки общих друзей не совпадают");

    }

    private User createTestUser() {
        return User.builder().id(1).email("first@mail.ru").login("FirstLogin").name("FirstUser")
                .birthday(LocalDate.of(1984, 2, 6)).build();
    }

    private User createSecondUser() {
        return User.builder().id(2).email("second@mail.ru").login("SecondLogin").name("SecondUser")
                .birthday(LocalDate.of(2000, 2, 20)).build();
    }

    private User createThirdUser() {
        return User.builder().id(3).email("third@mail.ru").login("ThirdLogin").name("ThirdUser")
                .birthday(LocalDate.of(1812, 2, 10)).build();
    }
}