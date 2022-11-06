package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> getUser(Long id) throws SQLException;

    User save (User user);

    void deleteUser(Long id);

    User updateUser(User user);

//    List<User> getFriends (long id) throws SQLException;
    List<User> getUsersBase() throws SQLException;


}
