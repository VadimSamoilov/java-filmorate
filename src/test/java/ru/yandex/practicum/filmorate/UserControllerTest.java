package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import javax.validation.ValidationException;

import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {

    private User user;
    private UserController userController;
    private DbUserStorage userStorage;

    // проверка контроллера при корректных атрибутах пользователя
    @Test
    public void shouldAddUserWhenAllAttributeCorrect() {
        User user1 = userController.createNewUser(user);
        assertEquals(user, user1, "Переданный и полученный пользователь должны совпадать");
        assertEquals(user1.getId(), userController.findAllUsers().size(), "В списке должен быть один пользователь");
    }

    // проверка контроллера при "пустой" электронной почте пользователя
    @Test
    public void shouldNoAddUserWhenUserEmailIsEmpty() {
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userController.createNewUser(user));
        assertEquals(0, userController.findAllUsers().size(), "Список пользователей должен быть пустым");
    }

    // проверка контроллера, когда электронная почта не содержит символа @
    @Test
    public void shouldNoAddUserWhenUserEmailIsNotContainsCommercialAt() {
        user.setEmail("notemail.ru");
        assertThrows(ValidationException.class, () -> userController.createNewUser(user));
        assertEquals(0, userController.findAllUsers().size(), "Список пользователей должен быть пустым");
    }

    // проверка контроллера, когда у пользователя пустой логин
    @Test
    public void shouldNoAddUserWhenUserLoginIsEmpty() {
        user.setLogin("");
        assertThrows(ValidationException.class, () -> userController.createNewUser(user));
        assertEquals(0, userController.findAllUsers().size(), "Список пользователей должен быть пустым");
    }

    // проверка контроллера, когда логин пользователя содержит пробелы
    @Test
    public void shouldNoAddUserWhenUserLoginIsContainsSpaces() {
        user.setLogin("Max Power");
        assertThrows(ValidationException.class, () -> userController.createNewUser(user));
        assertEquals(0, userController.findAllUsers().size(), "Список пользователей должен быть пустым");
    }

    // проверка контроллера, когда имя пользователя пустое
    @Test
    public void shouldAddUserWhenUserNameIsEmpty() {
        user.setName("");
        User user1 = userController.createNewUser(user);
        assertTrue(user1.getName().equals(user.getLogin()),
                "Имя и логин пользователя должны совпадать");
        assertEquals(1, userController.findAllUsers().size(), "В списке должен быть один пользователь");
    }

    // проверка контроллера, когда дата рождения пользователя в будущем
    @Test
    public void shouldAddUserWhenUserBirthdayInFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userController.createNewUser(user));
        assertEquals(0, userController.findAllUsers().size(), "Список пользователей должен быть пустым");
    }
}
