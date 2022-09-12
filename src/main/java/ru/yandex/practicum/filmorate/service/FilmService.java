package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;
    private final UserService userService;
    private Long id = 0L;
    public static final LocalDate RELISE = LocalDate.of(1895, Month.DECEMBER, 28);

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage, UserService userService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.userService = userService;
    }

    public List<Film> findAllFilmsInStorage() {
        return new ArrayList<>(inMemoryFilmStorage.getFilmBase().values());
    }

    public Film createNewFilm(Film film) {
        film.setId(++id);
        if (validateFilms(film)) {
            return inMemoryFilmStorage.create(film);
        } else throw new CustomValidationException("Ошибка при добавлении фильма");

    }

    public Film updateFilm(Film film) {
        if (validateFilms(film)) {
            id--;
            return inMemoryFilmStorage.update(film);
        } else throw new FilmNotFoundExeption("Ошибка при обновлении фильма");
    }

    public void deleteFilm(Long idFilm) {
        inMemoryFilmStorage.delete(findFilmById(idFilm));
    }

    public Film findFilmById(Long idFilm) {
        if (inMemoryFilmStorage.getFilm(idFilm) != null) {
            return inMemoryFilmStorage.getFilm(idFilm);
        } else throw new FilmNotFoundExeption("Фильм с данным ID не найден");
    }

    public void addLikeFilm(Long idFilm, Long idUser) {
        if (validatorFilmIdAndUserId(idFilm, idUser)) {
            findFilmById(idFilm).addlikeFilmUser(idUser);
            inMemoryFilmStorage.getFilm(idFilm).addLike();
        } else throw new CustomValidationException("Ошибка при добавлении Like. Проверьте правильность " +
                "введенных данных");
    }

    public void removeLikeFilm(Long idFilm, Long idUser) {
        if (validatorFilmIdAndUserId(idFilm, idUser)) {
            if (findFilmById(idFilm).getLikeFilmsIdUser().contains(idUser)) {
                findFilmById(idFilm).getLikeFilmsIdUser().remove(idUser);
                inMemoryFilmStorage.getFilm(idFilm).removeLike();
            } else throw new FilmNotFoundExeption("Фильм не найдет в избранном у пользователя");
        } else throw new CustomValidationException("Ошибка при удалении Like. Проверьте " +
                "правильность введенных данных");

    }

    public List<Film> findPopularFilm(Integer count) {
        return inMemoryFilmStorage.findPopularFilm(count);
    }

    public Boolean validatorFilmIdAndUserId(Long idFilm, Long idUser) {
        if ((idFilm > 0) && (idFilm != null) && (idUser > 0) && (idUser != null)) {
            if ((inMemoryFilmStorage.getFilm(idFilm) != null) && (userService.findUserById(idUser) != null)) {
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
}


