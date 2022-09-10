package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        return filmService.findAllFilmsInStorage();
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmService.createNewFilm(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/films")
    public void remove(@Valid @RequestBody Film film) {
        filmService.deleteFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film findById(@Valid @PathVariable("id") Long id) {
        return filmService.findFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        filmService.removeLikeFilm(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> findPopularFilm(@RequestParam(value = "count", defaultValue = "0", required = false) Integer count) {
        return filmService.findPopularFilm(count);
    }

}
