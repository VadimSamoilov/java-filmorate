package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundExeption;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage memoryUserStorage;

    public List<User> findAllUsers() {
        return new ArrayList<>(memoryUserStorage.getUsersBase().values());
    }

    public User createUser(User user) {
        return memoryUserStorage.createUser(user);
    }

    public User update(User user) {
        if (validatorName(user) && (checkingThePresenceOfUserInTheRepository(user.getId()))) {
            return memoryUserStorage.updateUser(user);
        } else {
            log.info("Ошибка при обновлении информации пользователя: " + user.toString());
            throw new UserNotFoundExeption("Введены некорректные данные пользователя. Пользователь не найден в базе");
        }
    }

    public User findUserById(Long idUser) {
        return Optional.ofNullable(memoryUserStorage.getUsersBase().get(idUser)).orElseThrow(() -> new UserNotFoundExeption("Данный пользователь не существует"));
    }

    // добавление пользователей в друзья
    public void addToFriendsByIdUsers(Long idUser, Long friendId) {
        if (validationNotNullAndFindUsers(idUser, friendId)) {
            if (!(memoryUserStorage.getUser(idUser).getFriendsId().contains(friendId))) {
                memoryUserStorage.getUser(idUser).addFriends(friendId);
                memoryUserStorage.getUser(friendId).addFriends(idUser);
            } else throw new CustomValidationException("Пользователи уже состоят в друзьях");
        } else throw new CustomValidationException("Неудалось добавить пользователей в друзья");
    }

    public void deleteFriendsByIdUsers(Long idUser, Long friendId) {
        if (validationNotNullAndFindUsers(idUser, friendId)) {
            if (memoryUserStorage.getUser(idUser).getFriendsId().contains(friendId)) {
                memoryUserStorage.getUser(idUser).getFriendsId().remove(friendId);
                memoryUserStorage.getUser(friendId).getFriendsId().remove(idUser);
            } else {
                throw new CustomValidationException("Пользователи не состоят в друзьях");
            }
        } else throw new CustomValidationException("Неудалось удалить пользователей из друзей");
    }

    // поиск всех друзей пользователя с определенным id
    public List<User> findUserIsFriends(Long idUser) {
        return memoryUserStorage.getFriends(idUser);
    }

    // поиск общих друзей с другим пользователем
    public List<User> findMutualFriendsWithTheUser(Long id, Long otherId) {
        return findUserById(id).getFriendsId().stream()
                .filter(findUserById(otherId).getFriendsId()::contains)
                .map(this::findUserById).collect(Collectors.toList());
    }

    private Boolean validationNotNullAndFindUsers(Long id, Long friendId) {
        if ((id > 0) && (id != null) && (friendId > 0) && (friendId != null)) {
            if ((checkingThePresenceOfUserInTheRepository(id)) && (checkingThePresenceOfUserInTheRepository(friendId))) {
                return true;
            } else throw new UserNotFoundExeption("Пользователи не найдены");
        } else throw new UserNotFoundExeption("Неверный формат данных");
    }

    private Boolean validatorName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() > 0) {
            return true;
        } else {
            return false;
        }

    }

    private Boolean checkingThePresenceOfUserInTheRepository(Long id) {
        if (memoryUserStorage.getUsersBase().containsKey(id)) {
            return true;
        }
        return false;
    }

}


