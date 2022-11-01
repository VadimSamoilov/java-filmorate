package ru.yandex.practicum.filmorate.storage.inmemorydb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.inmemorydb.UserStorage;

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

    public List<User> getUsersBase() {
        return new ArrayList<>(userBase.values());
    }

    @Override
    public User updateUser(User user) {
        log.info("Информация о пользователе " + user.toString() + " обновлена.");
        userBase.put(user.getUser_id(), user);
        return user;
    }

    // возвращаем список друзей пользователя
    @Override
    public List<User> getFriends(long id) {
        return getUser(id).get().getFriendsId()
                .stream()
                .map(this::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(userBase.get(id));
    }

    @Override
    public User createUser(User user) {
        user.setUser_id(++id);
        log.info("Создан новый пользователь: " + user.toString());
        userBase.put(id, user);
        return user;
    }
}

