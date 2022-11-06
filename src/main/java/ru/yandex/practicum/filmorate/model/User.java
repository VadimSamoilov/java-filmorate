package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class User {
    private Long id;
    @NotNull
    @Email(regexp = ".+[@].+[\\\\.].+", message = "Неверный формат Email")
    private String email;
    @NotBlank
    private String login;
    private String name;
    private Set<Long> friendsId;
    @PastOrPresent(message = "День рождения не может быть в будущем")
    @NotNull
    private  LocalDate birthday;

    // если имя пустое используем login
    public String getName() {
        return (name == null || name.isEmpty()) ? login : name;
    }

    public User(int id, String login, String name, String email, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friendsId = new HashSet<>();
    }

    public void addFriends(Long idFriend) {
        friendsId.add(idFriend);
    }

    public Set<Long> getFriendsId() {
        return friendsId;
    }

    public User() {

    }

}
