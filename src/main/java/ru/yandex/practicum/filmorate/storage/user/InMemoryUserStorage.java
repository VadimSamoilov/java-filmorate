package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Long id = Long.valueOf(0);
    private Map<Long, User> userBase = new HashMap<>();

    @Override
    public void deleteUser(Long id) {
//        if (userBase.containsKey(user.getId())){
//            userBase.remove(user);
//        }
//        else throw new CustomValidationException("Данный пользователь не существует");
    }

    public Map<Long, User> getUsersBase() {
        return userBase;
    }

    @Override
    public User updateUser(User user) {
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

    // возвращаем список друзей пользователя
    @Override
    public List<User> getFriends(long id) {
        User friend = getUser(id);
        List<Long> outputFriends = new ArrayList<>();
        List<User> userFriends = new ArrayList<>();
        if (friend != null) {
            if (friend.getFriendsId() != null) {
                outputFriends = friend.getFriendsId().stream().collect(Collectors.toList());
                for (Long id1:outputFriends){
                    userFriends.add(getUser(id1));
                }
                return userFriends;
            } else {
                throw new UserNotFoundExeption("У пользователя нет друзей");
            }
        } else throw new UserNotFoundExeption("Пользователь не найден");
    }


    @Override
    public User getUser(long id) {
        return userBase.get(id);
    }

    @Override
    public User createUser(User user) {
        user.setId(++id);
        if (validatorBirthDay(user)) {
            log.info("Создан новый пользователь: " + user.toString());
            userBase.put(id, user);
        }
        return user;
    }

    private Boolean validatorBirthDay(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if ((user.getBirthday().isBefore(LocalDate.now()) && user.getId() > 0)) {
            return true;
        } else {
            log.info("Ошибка при создании нового пользователя: " + user.toString());
            throw new UserNotFoundExeption("Введены некорректные данные пользователя");
        }

    }
}
