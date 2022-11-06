package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DBFriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final DBFriendsStorage dbFriendsStorage;
    private final DbUserStorage dbUserStorage;
    public List<User> findAllUsers() {
        return dbUserStorage.getUsersBase();
    }

    public User createUser(User user) {
        return dbUserStorage.save(user);
    }

    public User update(User user) {
        findUserById(user.getId());
        return dbUserStorage.updateUser(user);
    }

    public User findUserById(Long idUser) {
        return dbUserStorage.getUser(idUser)
                .orElseThrow(() -> new UserNotFoundExeption("Данный пользователь не существует"));
    }

    // добавление пользователей в друзья
    public void addToFriendsByIdUsers(Long idUser, Long friendId) {
        if (!(dbFriendsStorage.getFriends(idUser).contains(friendId))) {
           dbFriendsStorage.addFriend(idUser,friendId);
        } else throw new CustomValidationException("Пользователи уже состоят в друзьях");
    }

    public void deleteFriendsByIdUsers(Long idUser, Long friendId) {
        findUserById(idUser);
        User friend = findUserById(friendId);
        for (User user:dbFriendsStorage.getFriends(idUser)){
            if (user.getId() == friend.getId()){
                dbFriendsStorage.removeFriend(idUser,friendId);
            }else throw new CustomValidationException("Пользователи не состоят в друзьях");
        }
    }

    // поиск всех друзей пользователя с определенным id
    public List<User> findUserIsFriends(Long idUser) {
        return dbFriendsStorage.getFriends(idUser);
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


