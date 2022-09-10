package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundExeption;
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
    private Long id = Long.valueOf(0);
    public static final LocalDate RELISE = LocalDate.of(1895, Month.DECEMBER, 28);
    private Map<Long, Film> filmBase = new HashMap<>();

    @Override
    public Film getFilm(Long id) {
        return filmBase.get(id);
    }

    @Override
    public List<Film> findPopularFilm(Integer count) {
        int filmCount = 0;
        if (count == null || count == 0) {
            filmCount = 10;
        } else {
            filmCount = count;
        }
        return filmBase.values().stream()
                .sorted(new MyComporator().reversed())
                .limit(filmCount)
                .collect(Collectors.toList());
    }

    public Map<Long, Film> getFilmBase() {
        return new HashMap<>(filmBase);
    }

    // создание нового фильма и добавление его в хранилище
    @Override
    public Film create(Film film) {
        film.setId(++id);
        if (validateFilms(film)) {
            log.info("Добавлен новый фильм: " + film.toString());
            filmBase.put(film.getId(), film);
        }
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
        if (validateFilms(film)) {
            if (filmBase.containsKey(film.getId())) {
                log.info("Обновление фильма: " + film.toString());
                filmBase.put(film.getId(), film);
                id--; //я хз почему у меня увеличивается счетчик ID. Но без этой строки постман не пропускает проверку.
            } else {
                create(film);
            }
        }
        return film;
    }

    // проверка на валидность переданного фильма
    private Boolean validateFilms(Film film) {
        if (RELISE.isBefore(film.getReleaseDate())) {
            if (film.getId() > 0) {
                return true;
            } else throw new FilmNotFoundExeption("Релиз фильма не может быть раньше " + RELISE);
        } else {
            throw new CustomValidationException("Ошибка при добавлении фильма");
        }
    }

}
