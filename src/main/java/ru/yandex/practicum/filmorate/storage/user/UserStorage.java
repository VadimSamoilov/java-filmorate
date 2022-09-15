package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    Optional<User> getUser(long id);

    User createUser(User user);

    void deleteUser(Long id);

    User updateUser(User user);

    List<User> getFriends (long id);
    List<User> getUsersBase();


}
