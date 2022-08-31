package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exeption.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {

    private int id;
    public static final LocalDate RELISE = LocalDate.of(1895, Month.DECEMBER, 28);
    private Map<Integer, Film> filmBase = new HashMap<>();

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        return new ArrayList<>(filmBase.values());
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        film.setId(++id);
        if (validateFilms(film)) {
            log.info("Добавлен новый фильм: " + film.toString());
            filmBase.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        if (validateFilms(film)) {
            if (filmBase.containsKey(film.getId())) {
                log.info("Обновление фильма: " + film.toString());
                filmBase.put(film.getId(), film);
            } else {
                create(film);
            }
        }
        return film;
    }

    private Boolean validateFilms(Film film) {
        if (RELISE.isBefore(film.getReleaseDate()) && film.getId() > 0) {
            return true;
        } else {
            log.info("Не удалось добавить фильм: " + film.toString() + " в базу.");
            throw new CustomValidationException("Ошибка при добавлении фильма");
        }
    }
}
