package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

    private User user1;

    // разобрать тесты
    private FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
            new UserService(new InMemoryUserStorage())));
    private UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

    @Test
    void dateOfBirthIsInTheFutureAndEmptyLogin() {
        CustomValidationException customExeption = Assertions.assertThrows(CustomValidationException.class, () -> {
            userController.createNewUser(new User("vadim@smartpe.ru", "", "vadim",
                    LocalDate.of(2030, 8, 29))) ;
        }, "Создан новый пользователь");

        Assertions.assertEquals("Введены некорректные данные пользователя", customExeption.getMessage());
    }


    @Test
    void releaseDateBeyondLimits() {
        CustomValidationException customExeption = Assertions.assertThrows(CustomValidationException.class, () -> {
            filmController.create(new Film("5",  LocalDate.of(1894, 6, 11),"Фильм классный, там тонет Дикаприо",
                    110,4));
        }, "Добавлен новый фильм");

        Assertions.assertEquals("Ошибка при добавлении фильма", customExeption.getMessage());
    }
}
