package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
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
        findUserById(user.getId());
        return memoryUserStorage.updateUser(user);
    }

    public User findUserById(Long idUser) {
        return memoryUserStorage.getUser(idUser)
                .orElseThrow(() -> new UserNotFoundExeption("Данный пользователь не существует"));
    }

    // добавление пользователей в друзья
    public void addToFriendsByIdUsers(Long idUser, Long friendId) {
        if (!(findUserById(idUser).getFriendsId().contains(friendId))) {
            findUserById(idUser).addFriends(friendId);
            findUserById(friendId).addFriends(idUser);
        } else throw new CustomValidationException("Пользователи уже состоят в друзьях");
    }

    public void deleteFriendsByIdUsers(Long idUser, Long friendId) {
        if (findUserById(idUser).getFriendsId().contains(friendId)) {
            findUserById(idUser).getFriendsId().remove(friendId);
            findUserById(friendId).getFriendsId().remove(idUser);
        } else throw new CustomValidationException("Пользователи не состоят в друзьях");

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

    //проверка поля name
    private void validatorName(User user) {
        if (findUserById(user.getId()).getName() == null || findUserById(user.getId()).getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}


