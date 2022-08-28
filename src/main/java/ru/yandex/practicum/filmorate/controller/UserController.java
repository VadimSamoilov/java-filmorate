package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


@RestController

public class UserController {
    private int id;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private HashMap<Integer, User> userBase = new HashMap<>();


    @GetMapping("/users")
    public List<User> findAllUsers() {
        return new ArrayList<>(userBase.values());
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws CustomValidationException {
        if (validatorBirthDay(user)) {
            user.setId(++id);
            log.info("Создан новый пользователь: " + user.toString());
            userBase.put(id, user);
            return user;
        } else {
            log.info("Ошибка при создании нового пользователя: " + user.toString());
            throw new CustomValidationException("Введены некорректные данные пользователя");

        }
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws CustomValidationException {
        if (validatorBirthDay(user) && user.getId() > 0) {
            if (userBase.containsKey(user.getId())) {
                log.info("Информация о пользователе " + user.toString() + " обновлена.");
                userBase.put(user.getId(), user);
                return user;
            } else {
                createUser(user);
                return user;
            }
        } else {
            throw new CustomValidationException("Введены некорректные данные пользователя");
        }
    }


    private Boolean validatorBirthDay(User user) {
        return !(user.getBirthday().isAfter(LocalDate.now()))
                && !user.getLogin().isBlank()
                && !user.getLogin().contains(" ")
                && !(user.getLogin() == null)
                && !(user.getName().isEmpty())
                && !user.getEmail().isBlank()
                && Pattern.compile("(.+@.+\\..+)").matcher(user.getEmail()).matches();
    }
}
