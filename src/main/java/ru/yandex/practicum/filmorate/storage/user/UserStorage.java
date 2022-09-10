package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User getUser(long id);

    User createUser(User user);

    void deleteUser(Long id);

    User updateUser(User user);

    List<User> getFriends (long id);


}
