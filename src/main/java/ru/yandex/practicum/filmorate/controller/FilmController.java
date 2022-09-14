package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

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
    public void remove(@Valid @RequestBody Long ifFilm) {
        filmService.deleteFilm(ifFilm);
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
    public List<Film> findPopularFilm(@RequestParam(value = "count", defaultValue = "10", required = false) @Positive Integer count) {
        return filmService.findPopularFilm(count);
    }

}
