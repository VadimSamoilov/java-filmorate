package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmsStorage {

    Film create(Film film);

    void delete(Film film);

    Film update(Film film);

    Optional<Film> getFilm(Long id);


    List<Film> getFilmBase();

    void saveGenre(Film film);


}
