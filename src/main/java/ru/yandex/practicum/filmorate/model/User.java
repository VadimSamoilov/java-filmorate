package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private  Integer id;
    @Email(regexp = ".+[@].+[\\\\.].+",message = "Неверный формат Email")
    private  String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent(message = "День рождения не может быть в будущем")
    @NotNull
    private final LocalDate birthday;

// если имя пустое используем login
    public String getName() {
        return (name==null || name.isEmpty())? login : name;
    }

    public User(String login, String name, String email, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
