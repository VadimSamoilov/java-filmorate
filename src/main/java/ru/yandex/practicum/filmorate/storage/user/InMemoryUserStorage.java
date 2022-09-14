package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundExeption;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Long id = 0L;
    private Map<Long, User> userBase = new HashMap<>();

    @Override
    public void deleteUser(Long id) {
        if (userBase.containsKey(id)) {
            userBase.remove(getUser(id));
        } else throw new CustomValidationException("Данный пользователь не существует");
    }

    public Map<Long, User> getUsersBase() {
        return new HashMap<>(userBase);
    }

    @Override
    public User updateUser(User user) {
            log.info("Информация о пользователе " + user.toString() + " обновлена.");
            userBase.put(user.getId(), user);
            return user;
    }

    // возвращаем список друзей пользователя
    @Override
    public List<User> getFriends(long id) {
        return getUser(id).getFriendsId().stream()
                .map(this::getUser).collect(Collectors.toList());
    }


    @Override
    public User getUser(long id) {
            return Optional.of(userBase.get(id)).get();
    }

    @Override
    public User createUser(User user) {
        user.setId(++id);
        log.info("Создан новый пользователь: " + user.toString());
        userBase.put(id, user);
        return user;
    }
}

