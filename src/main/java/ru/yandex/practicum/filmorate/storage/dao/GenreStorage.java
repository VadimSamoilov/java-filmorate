package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getGenre (long genreId);

    void add(Film film);

    List<Genre> getAll();

    List<Genre> getFilmGenres(Long filmId);
}
