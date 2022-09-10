package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    void delete(Film film);

    Film update(Film film);

    Film getFilm(Long id);

    List<Film> findPopularFilm(Integer count);

}