package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class Film {
    private static final int LENGTH_DESCRIPTION = 200;

    private Long film_id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private final String title;
    @Size(min = 0, max = LENGTH_DESCRIPTION, message = "Описание дожно быть не больше " + LENGTH_DESCRIPTION + " символов")
    private String description;
    @NotNull(message = "Дата релиза не может быть пустой")
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительсность фильма болжна быть положительной")
    private double duration;
    private Set<Long> likeFilmsIdUser;
    private Integer rate;
    private Set<Genre> genre;
    private MPA mpa;

    public Film(Long film_id, String title, String description, LocalDate releaseDate,
                double duration, Integer rate, MPA mpa) {
        this.film_id = film_id;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
    }

    public void setFilm_id(Long film_id) {
        this.film_id = film_id;
    }

    public Film(long l, String title, String description, LocalDate releaseDate, double duration,
                MPA mpa) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Set<Long> getLikeFilmsIdUser() {
        return likeFilmsIdUser;
    }

    public Film(String title, LocalDate releaseDate, String description, double duration, int rate) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.likeFilmsIdUser = new HashSet<>();

    }

    public void addGenre (Genre genres){
        genre.add(genres);
    }

    public int getRate() {
        return rate;
    }


    public void addlikeFilmUser(Long idUser) {
        likeFilmsIdUser.add(idUser);
        rate =likeFilmsIdUser.size();
    }

    public void removeLike(Long id) {
        likeFilmsIdUser.remove(id);
        rate =likeFilmsIdUser.size();
    }
}
