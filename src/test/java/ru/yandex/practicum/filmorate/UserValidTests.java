package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
class UserValidTests {

	public UserController userController = new UserController();

	@Test
	void simpleUserAddTest() {
		User userForTest = new User(1, "userEmail@mail.ru",
				"login", "name", LocalDate.of(1990,12,28));
		User user = userController.create(userForTest);
		Assertions.assertEquals(user, userForTest);
	}

	//электронная почта не может быть пустой
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

	//эл почта должна содержать символ @;
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

	//логин не может быть пустым
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

	//логин не может содержать пробелы;
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

	//имя для отображения может быть пустым — в таком случае будет использован логин;
	@Test
	void AddUserWithNullNameTest() {
		User userForTest = new User(1, "userEmail@mail.ru",
				"login", "", LocalDate.of(1990,12,28));
		User user = userController.create(userForTest);
		Assertions.assertEquals(user.getName(), userForTest.getLogin());
	}

	//дата рождения не может быть в будущем
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
