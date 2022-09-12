package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(inMemoryUserStorage.getUsersBase().values());
    }

    public User createUser(User user) {
        return inMemoryUserStorage.createUser(user);
    }

    public User update(User user) {
        if (validatorBirthDay(user)) {
            return inMemoryUserStorage.updateUser(user);
        } else {
            log.info("Ошибка при создании нового пользователя: " + user.toString());
            throw new UserNotFoundExeption("Введены некорректные данные пользователя");
        }
    }

    public User findUserById(Long idUser) {
        if (inMemoryUserStorage.getUsersBase().containsKey(idUser)) {
            return inMemoryUserStorage.getUsersBase().get(idUser);
        } else throw new UserNotFoundExeption("Данный пользователь не существует");
    }

    // добавление пользователей в друзья
    public void addToFriendsByIdUsers(Long idUser, Long friendId) {
        if (validationNotNullAndFindUsers(idUser, friendId)) {
            if (!(inMemoryUserStorage.getUser(idUser).getFriendsId().contains(friendId))) {
                inMemoryUserStorage.getUser(idUser).addFriends(friendId);
                inMemoryUserStorage.getUser(friendId).addFriends(idUser);
            } else throw new CustomValidationException("Пользователи уже состоят в друзьях");
        } else throw new CustomValidationException("Неудалось добавить пользователей в друзья");
    }

    public void deleteFriendsByIdUsers(Long idUser, Long friendId) {
        if (validationNotNullAndFindUsers(idUser, friendId)) {
            if (inMemoryUserStorage.getUser(idUser).getFriendsId().contains(friendId)) {
                inMemoryUserStorage.getUser(idUser).getFriendsId().remove(friendId);
                inMemoryUserStorage.getUser(friendId).getFriendsId().remove(idUser);
            } else {
                throw new CustomValidationException("Пользователи не состоят в друзьях");
            }
        } else throw new CustomValidationException("Неудалось удалить пользователей из друзей");
    }

    // поиск всех друзей пользователя с определенным id
    public List<User> findUserIsFriends(Long idUser) {
        return inMemoryUserStorage.getFriends(idUser);
    }

    // поиск общих друзей с другим пользователем
    public List<User> findMutualFriendsWithTheUser(Long id, Long otherId) {
        return findUserById(id).getFriendsId().stream().filter(findUserById(otherId).getFriendsId()::contains).map(this::findUserById).collect(Collectors.toList());
    }

    private Boolean validationNotNullAndFindUsers(Long id, Long friendId) {
        if ((id > 0) && (id != null) && (friendId > 0) && (friendId != null)) {
            if ((inMemoryUserStorage.getUser(id) != null) && (inMemoryUserStorage.getUser(friendId) != null)) {
                return true;
            } else throw new UserNotFoundExeption("Пользователи не найдены");
        } else throw new UserNotFoundExeption("Неверный формат данных");
    }

    private Boolean validatorBirthDay(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() > 0) {
            return true;
        } else {
            return false;
        }

    }

}


