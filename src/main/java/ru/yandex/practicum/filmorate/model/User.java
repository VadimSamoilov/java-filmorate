package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class User {
    private Long user_id;
    @NotNull
    @Email(regexp = ".+[@].+[\\\\.].+", message = "Неверный формат Email")
    private String email;
    @NotBlank
    private String login;
    private String name;
    private Set<Long> friendsId;

    @PastOrPresent(message = "День рождения не может быть в будущем")
    @NotNull
    private LocalDate birth_day;

    public User() {

    }


    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public void setBirth_day(LocalDate birth_day) {
        this.birth_day = birth_day;
    }

    public User(Long user_id, String login, String name, String email, LocalDate birth_day) {
        this.user_id = user_id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.friendsId = friendsId;
        this.birth_day = birth_day;
    }



    // если имя пустое используем login
    public String getName() {
        return (name == null || name.isEmpty()) ? login : name;
    }

    public User(String login, String name, String email, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birth_day = birthday;
        this.friendsId = new HashSet<>();
    }

    public void addFriends(Long idFriend) {
        friendsId.add(idFriend);
    }

    public Set<Long> getFriendsId() {
        return friendsId;
    }

}
