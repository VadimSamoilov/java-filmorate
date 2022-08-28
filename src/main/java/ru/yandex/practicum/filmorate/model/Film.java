package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;



@Data
@AllArgsConstructor
public class Film {
    public static final int LENGTH_DESCRIPTION = 200;

    public Film(String name, String description, LocalDate releaseDate, double duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    @NotNull
    @Min(1)
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private final String name;
    @Size(min=0,max= LENGTH_DESCRIPTION,message = "Описание дожно быть не больше "+ LENGTH_DESCRIPTION + " символов")
    private String description;
    @NotNull(message = "Дата релиза не может быть пустой")
    private final LocalDate releaseDate;
    @Positive (message = "Продолжительсность фильма болжна быть положительной")
    private double duration;

}
