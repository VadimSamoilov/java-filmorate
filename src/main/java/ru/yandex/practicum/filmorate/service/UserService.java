package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DBFriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {

    // private final UserStorage memoryUserStorage;
    private final DBFriendsStorage dbFriendsStorage;
    private final DbUserStorage dbUserStorage;


    public UserService(DBFriendsStorage dbFriendsStorage, DbUserStorage dbUserStorage) {
        this.dbFriendsStorage = dbFriendsStorage;
        this.dbUserStorage = dbUserStorage;
    }

    public List<User> findAllUsers() {
        return dbUserStorage.getUsersBase();
    }

    public User createUser(User user) {
        return dbUserStorage.createUser(user);
    }

    public User update(User user) {
        validatorName(user);
        findUserById(user.getUser_id());
        return dbUserStorage.updateUser(user);
    }

    public User findUserById(Long idUser) {
        return dbUserStorage.getUser(idUser)
                .orElseThrow(() -> new UserNotFoundExeption("Данный пользователь не существует"));
    }

    // добавление пользователей в друзья
    public void addToFriendsByIdUsers(Long idUser, Long friendId) {
        dbFriendsStorage.addFriend(idUser,friendId);
    }

    public void deleteFriendsByIdUsers(Long idUser, Long friendId) {
        dbFriendsStorage.removeFriend(idUser,friendId);
    }

    // поиск всех друзей пользователя с определенным id
    public List<User> findUserIsFriends(Long idUser) {
        return dbFriendsStorage.getFriends(idUser);
    }

    // поиск общих друзей с другим пользователем
    public List<User> findMutualFriendsWithTheUser(Long id, Long otherId) {
        return dbFriendsStorage.getCommonFriends(id,otherId);
    }

    //проверка поля name
    private void validatorName(User user) {
        if (findUserById(user.getUser_id()).getName() == null || findUserById(user.getUser_id()).getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}


