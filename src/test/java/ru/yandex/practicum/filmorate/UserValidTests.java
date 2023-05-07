package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

@SpringBootTest
class UserValidTests {

	public UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

	@Test
	void simpleUserAddTest() {
		User userForTest = new User(1, "userEmail@mail.ru",
				"login", "name", LocalDate.of(1990,12,28));
		User user = userController.create(userForTest);
		Assertions.assertEquals(user, userForTest);
	}

	@Test
	void addUserWithoutEmailTest() {
		User userForTest = new User(1, "",
				"login", "name", LocalDate.of(1990,12,28));
		RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				User user = userController.create(userForTest);
			}
		});

		Assertions.assertEquals("В переданных данных отсутствует адрес электронной почты.", exception.getMessage());
	}

	@Test
	void addUserWithBlankEmailTest() {
		User userForTest = new User(1, " ",
				"login", "name", LocalDate.of(1990,12,28));
		RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				User user = userController.create(userForTest);
			}
		});
		Assertions.assertEquals("В переданных данных отсутствует адрес электронной почты.", exception.getMessage());
	}

	@Test
	void addUserWithoutCharInEmailTest() {
		User userForTest = new User(1, "userEmailmail.ru",
				"login", "name", LocalDate.of(1990,12,28));
		RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				User user = userController.create(userForTest);
			}
		});
		Assertions.assertEquals("В адресе электронной почты отсутствует символ @.", exception.getMessage());
	}

	@Test
	void addUserWithoutLoginTest() {
		User userForTest = new User(1, "userEmail@mail.ru",
				"", "name", LocalDate.of(1990,12,28));
		RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				User user = userController.create(userForTest);
			}
		});
		Assertions.assertEquals("Логин не может быть пустым или содержать пробелы.", exception.getMessage());
	}

	@Test
	void addUserWithSpaceInLoginTest() {
		User userForTest = new User(1, "userEmail@mail.ru",
				" login", "name", LocalDate.of(1990,12,28));
		RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				User user = userController.create(userForTest);
			}
		});
		Assertions.assertEquals("Логин не может быть пустым или содержать пробелы.", exception.getMessage());
	}

	@Test
	void addUserWithNullNameTest() {
		User userForTest = new User(1, "userEmail@mail.ru",
				"login", "", LocalDate.of(1990,12,28));
		User user = userController.create(userForTest);
		Assertions.assertEquals(user.getName(), userForTest.getLogin());
	}

	@Test
	void addUserWithWrongBirthdayTest() {
		User userForTest = new User(1, "userEmail@mail.ru",
				"login", "name", LocalDate.of(2023,12,28));
		RuntimeException exception = Assertions.assertThrows(RuntimeException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				User user = userController.create(userForTest);
			}
		});
		Assertions.assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
	}
}