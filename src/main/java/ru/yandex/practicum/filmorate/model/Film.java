package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import javax.validation.constraints.*;

@Data

public class Film {
    private static final int LENGTH_DESCRIPTION = 200;

    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private final String name;
    @Size(min = 0, max = LENGTH_DESCRIPTION, message = "Описание дожно быть не больше " + LENGTH_DESCRIPTION + " символов")
    private String description;
    @NotNull(message = "Дата релиза не может быть пустой")
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительсность фильма болжна быть положительной")
    private double duration;

    private Set<Long> likeFilmsIdUser;
    private int rate;


    public Set<Long> getLikeFilmsIdUser() {
        return likeFilmsIdUser;
    }

    public Film(String name, LocalDate releaseDate, String description, double duration, int rate) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.likeFilmsIdUser = new HashSet<>();

    }

    public void addLike() {
        rate++;
    }

    public int getRate() {
        return rate;
    }

    public void removeLike() {
        rate--;
    }

    public void addlikeFilmUser(Long idUser) {
        likeFilmsIdUser.add(idUser);
    }

    public void removeLike(Long id) {
        likeFilmsIdUser.remove(id);
    }
}
