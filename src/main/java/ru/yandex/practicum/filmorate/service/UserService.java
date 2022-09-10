package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(inMemoryUserStorage.getUsersBase().values());
    }

    public User createUser(User user) {
        return inMemoryUserStorage.createUser(user);
    }

    public User update(User user) {
        return inMemoryUserStorage.updateUser(user);
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
        List<User> mutualFriends = new ArrayList<>();
        if (validationNotNullAndFindUsers(id, otherId)) {
            List<User> oneUser = findUserIsFriends(id);
            List<User> friend = findUserIsFriends(otherId);
            if (!(oneUser.isEmpty()) && !(friend.isEmpty())) {
                for (int i = 0; i < oneUser.size(); i++) {
                    for (int j = 0; j < friend.size(); j++) {
                        if (oneUser.get(i).getId().equals(friend.get(j).getId())) {
                            mutualFriends.add(oneUser.get(i));
                        }
                    }
                }
            }return mutualFriends;
        } else throw new CustomValidationException("Ошибка входящих данных");
    }

        private Boolean validationNotNullAndFindUsers (Long id, Long friendId){
            if ((id > 0) && (id != null) && (friendId > 0) && (friendId != null)) {
                if ((inMemoryUserStorage.getUser(id) != null) && (inMemoryUserStorage.getUser(friendId) != null)) {
                    return true;
                } else throw new UserNotFoundExeption("Пользователи не найдены");
            } else throw new UserNotFoundExeption("Неверный формат данных");
        }
    }


