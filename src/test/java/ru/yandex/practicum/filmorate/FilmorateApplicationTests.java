package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

    private User user1;
    private FilmController filmController = new FilmController();
    private UserController userController = new UserController();

    @Test
    void dateOfBirthIsInTheFutureAndEmptyLogin() {
        CustomValidationException customExeption = Assertions.assertThrows(CustomValidationException.class, () -> {
            userController.createUser(new User("vadim@smartpe.ru", "", "vadim",
                    LocalDate.of(2030, 8, 29)));
        }, "Создан новый пользователь");

        Assertions.assertEquals("Введены некорректные данные пользователя", customExeption.getMessage());
    }


    @Test
    void releaseDateBeyondLimits() {
        CustomValidationException customExeption = Assertions.assertThrows(CustomValidationException.class, () -> {
            filmController.create(new Film("", "Фильм классный, там тонет Дикаприо",
                    LocalDate.of(1894, 6, 11), 110));
        }, "Добавлен новый фильм");

        Assertions.assertEquals("Ошибка при добавлении фильма", customExeption.getMessage());
    }
}
