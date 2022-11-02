package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    void delete(Film film);

    Film update(Film film);

    Optional<Film> getFilm(Long id);

    List<Film> findPopularFilm(Integer count);

    List<Film> getFilmBase();

}
