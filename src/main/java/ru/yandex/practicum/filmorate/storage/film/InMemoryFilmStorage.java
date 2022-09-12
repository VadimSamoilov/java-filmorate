package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.MyComporator;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j

public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> filmBase = new HashMap<>();

    @Override
    public Film getFilm(Long id) {
        return filmBase.get(id);
    }

    @Override
    public List<Film> findPopularFilm(Integer count) {
        return filmBase.values().stream()
                .sorted(new MyComporator().reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Film> getFilmBase() {
        return new HashMap<>(filmBase);
    }

    // создание нового фильма и добавление его в хранилище
    @Override
    public Film create(Film film) {
        log.info("Добавлен новый фильм: " + film.toString());
        filmBase.put(film.getId(), film);
        return film;
    }

    // Удаление фильма из хранилища
    @Override
    public void delete(Film film) {
        if (filmBase.containsKey(film.getId())) {
            filmBase.remove(film.getId());
        } else throw new CustomValidationException("Невозможно удалить фильм: не найдем в базе");
    }

    //Обновление информации о фильме
    @Override
    public Film update(Film film) {
        if (filmBase.containsKey(film.getId())) {
            log.info("Обновление фильма: " + film.toString());
            filmBase.put(film.getId(), film);
        } else {
            create(film);
        }
        return film;
    }

}
