package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundExeption;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;


import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage memoryFilmStorage;
    private final UserService userService;
    private Long id = 0L;
    public static final LocalDate RELISE = LocalDate.of(1895, Month.DECEMBER, 28);


    public List<Film> findAllFilmsInStorage() {
        return new ArrayList<>(memoryFilmStorage.getFilmBase().values());
    }

    public Film createNewFilm(Film film) {
        film.setId(++id);
        if (validateFilms(film)) {
            return memoryFilmStorage.create(film);
        } else throw new CustomValidationException("Ошибка при добавлении фильма");

    }

    public Film updateFilm(Film film) {
        if (validateFilms(film) && (validatorPresenceOfTheMovieITheStorage(film))) {
            id--;
            return memoryFilmStorage.update(film);
        } else
            log.info(film.toString());
        throw new FilmNotFoundExeption("Ошибка при обновлении фильма. Фильм "
                + film.getName() + " не найден в базе");
    }

    public void deleteFilm(Long idFilm) {
        if (validatorPresenceOfTheMovieITheStorage(findFilmById(idFilm))) {
            memoryFilmStorage.delete(findFilmById(idFilm));
        } else throw new FilmNotFoundExeption("Невозможно удалить фильм. "
                + findFilmById(idFilm).getName() + " не найден в базе.");
    }

    public Film findFilmById(Long idFilm) {
        return Optional.ofNullable(memoryFilmStorage.getFilmBase().get(idFilm)).orElseThrow(() -> new FilmNotFoundExeption("Фильм с данным ID не найден"));

    }

    public void addLikeFilm(Long idFilm, Long idUser) {
        if (validatorFilmIdAndUserId(idFilm, idUser)) {
            findFilmById(idFilm).addlikeFilmUser(idUser);
        } else throw new CustomValidationException("Ошибка при добавлении Like. Проверьте правильность " +
                "введенных данных");
    }

    public void removeLikeFilm(Long idFilm, Long idUser) {
        if (validatorFilmIdAndUserId(idFilm, idUser)) {
            if (findFilmById(idFilm).getLikeFilmsIdUser().contains(idUser)) {
                findFilmById(idFilm).removeLike(idUser);
            } else throw new FilmNotFoundExeption("Фильм не найдет в избранном у пользователя");
        } else throw new CustomValidationException("Ошибка при удалении Like. Проверьте " +
                "правильность введенных данных");

    }

    public List<Film> findPopularFilm(Integer count) {
        return memoryFilmStorage.findPopularFilm(count);
    }

    public Boolean validatorFilmIdAndUserId(Long idFilm, Long idUser) {
        if ((idFilm > 0) && (idFilm != null) && (idUser > 0) && (idUser != null)) {
            if ((memoryFilmStorage.getFilm(idFilm) != null) && (userService.findUserById(idUser) != null)) {
                return true;
            } else throw new FilmNotFoundExeption("Невозможно найти пользователя и фильм");
        } else throw new UserNotFoundExeption("Неверный формат данных");
    }

    // проверка на валидность переданного фильма
    private Boolean validateFilms(Film film) {
        if (RELISE.isBefore(film.getReleaseDate())) {
            if (film.getId() >= 0) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new CustomValidationException("Релиз фильма не может быть раньше " + RELISE);
        }
    }

    private Boolean validatorPresenceOfTheMovieITheStorage(Film film) {
        if (memoryFilmStorage.getFilmBase().containsKey(film.getId())) {
            return true;
        } else {
            return false;
        }
    }
}


