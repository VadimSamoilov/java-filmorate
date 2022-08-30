package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;
import java.util.regex.Pattern;


@RestController
@Slf4j

public class UserController {
    private int id;
    private Map<Integer, User> userBase = new HashMap<>();


    @GetMapping("/users")
    public List<User> findAllUsers() {
        return new ArrayList<>(userBase.values());
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        user.setId(++id);
        if (validatorBirthDay(user)) {
            log.info("Создан новый пользователь: " + user.toString());
            userBase.put(id, user);
        }
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        if (validatorBirthDay(user)) {
            if (userBase.containsKey(user.getId())) {
                log.info("Информация о пользователе " + user.toString() + " обновлена.");
                userBase.put(user.getId(), user);
            } else {
                createUser(user);
            }
        }
        return user;
    }


    private Boolean validatorBirthDay(User user) {
        if ((user.getBirthday().isBefore(LocalDate.now()) && user.getId() > 0) && user.getName() !=null) {
            return true;
        } else {
            log.info("Ошибка при создании нового пользователя: " + user.toString());
            throw new CustomValidationException("Введены некорректные данные пользователя");
        }

    }
}
