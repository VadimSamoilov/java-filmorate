package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.MyComporator;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j

public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> filmBase = new HashMap<>();
    private Long id = 0L;
    @Override
    public Optional<Film> getFilm(Long id) {
        log.info(String.valueOf(filmBase.get(id)));
        return Optional.ofNullable(filmBase.get(id));
    }

    @Override
    public List<Film> findPopularFilm(Integer count) {
        return filmBase.values().stream()
                .sorted(new MyComporator())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getFilmBase() {
        return new ArrayList<>(filmBase.values());
    }

    // создание нового фильма и добавление его в хранилище
    @Override
    public Film create(Film film) {
        log.info("Добавлен новый фильм: " + film.toString());
        film.setId(++id);
        filmBase.put(film.getId(), film);
        return film;
    }

    // Удаление фильма из хранилища
    @Override
    public void delete(Film film) {
        filmBase.remove(film.getId());
    }

    //Обновление информации о фильме
    @Override
    public Film update(Film film) {
        log.info("Обновление фильма: " + film.toString());
        delete(film);
        filmBase.put(film.getId(), film);
        return film;
    }

}
