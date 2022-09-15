package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundExeption;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage memoryUserStorage;

    public List<User> findAllUsers() {
        return memoryUserStorage.getUsersBase();
    }

    public User createUser(User user) {
        return memoryUserStorage.createUser(user);
    }

    public User update(User user) {
        validatorName(user);
        checkingThePresenceOfUserInTheRepository(user);
        return memoryUserStorage.updateUser(user);
    }

    public User findUserById(Long idUser) {
        return Optional.ofNullable(idUser).map(memoryUserStorage::getUser).get()
                .orElseThrow(() -> new UserNotFoundExeption("Данный пользователь не существует"));
    }

    // добавление пользователей в друзья
    public void addToFriendsByIdUsers(Long idUser, Long friendId) {
        validationNotNullAndFindUsers(idUser, friendId);
        if (!(memoryUserStorage.getUser(idUser).get().getFriendsId().contains(friendId))) {
            memoryUserStorage.getUser(idUser).get().addFriends(friendId);
            memoryUserStorage.getUser(friendId).get().addFriends(idUser);
        } else throw new CustomValidationException("Пользователи уже состоят в друзьях");
    }

    public void deleteFriendsByIdUsers(Long idUser, Long friendId) {
        validationNotNullAndFindUsers(idUser, friendId);
        if (memoryUserStorage.getUser(idUser).get().getFriendsId().contains(friendId)) {
            memoryUserStorage.getUser(idUser).get().getFriendsId().remove(friendId);
            memoryUserStorage.getUser(friendId).get().getFriendsId().remove(idUser);
        } else {
            throw new CustomValidationException("Пользователи не состоят в друзьях");
        }
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

    // проверка при добавлении пользователя в друзья
    private void validationNotNullAndFindUsers(Long idUser, Long friendId) {
        Optional.ofNullable(idUser).filter(p -> (p > 0) && (p != null))
                .orElseThrow(() -> new UserNotFoundExeption("Неверный формат данных"));
        Optional.ofNullable(friendId).filter(p -> (p > 0) && (p != null))
                .orElseThrow(() -> new UserNotFoundExeption("Неверный формат данных"));
        Optional.ofNullable(idUser).filter(p -> memoryUserStorage.getUser(p) != null)
                .orElseThrow(() -> new UserNotFoundExeption("Пользователь не существует"));
        Optional.ofNullable(friendId).filter(p -> memoryUserStorage.getUser(p) != null)
                .orElseThrow(() -> new UserNotFoundExeption("Пользователь не существует"));

    }

    //проверка поля name
    private void validatorName(User user) {
        Optional.ofNullable(user).map(User::getId).filter(p -> p > 0)
                .orElseThrow(() -> new UserNotFoundExeption("ID не может быть отрицательным."));
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    // проверка есть ли пользователь в базе
    private void checkingThePresenceOfUserInTheRepository(User user) {
        Optional.ofNullable(user).map(User::getId).filter(p -> findUserById(p) != null)
                .orElseThrow(() -> new FilmNotFoundExeption("Ошибка валидации фильма"));
    }

}


