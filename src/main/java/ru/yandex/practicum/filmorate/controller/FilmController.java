package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exeption.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class FilmController {

    private int id;
    public static final LocalDate RELISE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private HashMap<Integer, Film> filmBase = new HashMap<>();

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        return new ArrayList<>(filmBase.values());
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        if (validateRelies(film)) {
            log.info("Добавлен новый фильм: "+film.toString());
            film.setId(++id);
            filmBase.put(film.getId(), film);
            return film;
        } else {
            log.info("Не удалось добавить фильм: " + film.toString() + " в базу.");
             throw new CustomValidationException("Ошибка при добавлении фильма");
        }
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        if (filmBase.containsKey(film.getId())&&validateRelies(film)) {
            log.info("Обновление фильма: "+ film.toString());
            filmBase.put(film.getId(), film);
        } else {
            create(film);
        }
        return film;
    }

    private Boolean validateRelies(Film film) {
        return RELISE.isBefore(film.getReleaseDate())
                && !film.getName().isBlank()
                && !(film.getDuration()<0)
                && !(film.getDescription().length()>200);
    }

}
