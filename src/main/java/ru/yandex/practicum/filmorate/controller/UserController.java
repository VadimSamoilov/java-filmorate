package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;

import java.util.List;


@RestController

public class UserController {

        private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/users")
    public User createNewUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        return userService.update(user);
    }


    @GetMapping("/users/{id}")
    public User findUserById(@Valid @PathVariable("id") Long userId) {
        return userService.findUserById(userId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable("id") Long id, @PathVariable("friendId") Long friendId) {
        userService.addToFriendsByIdUsers(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriendsById(@PathVariable("id") Long id,
                                  @PathVariable("friendId") Long friendId) {
        userService.deleteFriendsByIdUsers(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> userIsFriendsList(@PathVariable("id") Long id) {
        return userService.findUserIsFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable("id") Long id,
                                        @PathVariable("otherId") Long otherId){
        return userService.findMutualFriendsWithTheUser(id, otherId);
    }

}
