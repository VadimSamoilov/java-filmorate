package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundExeption;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, UserService userService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.userService = userService;
    }

    public List<Film> findAllFilmsInStorage() {
        return new ArrayList<>(inMemoryFilmStorage.getFilmBase().values());
    }

    public Film createNewFilm(Film film) {
        return inMemoryFilmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.update(film);
    }

    public void deleteFilm(Film film) {
        inMemoryFilmStorage.delete(film);
    }

    public Film findFilmById(Long idFilm) {
        if (inMemoryFilmStorage.getFilm(idFilm) != null) {
            return inMemoryFilmStorage.getFilm(idFilm);
        } else throw new FilmNotFoundExeption("Фильм с данным ID не найден");
    }

    public void addLikeFilm(Long idFilm, Long idUser) {
        if (validatorFilmIdAndUserId(idFilm, idUser)) {
            userService.findUserById(idUser).addlikeFilm(idFilm);
            inMemoryFilmStorage.getFilm(idFilm).addLike();
        } else throw new CustomValidationException("Ошибка при добавлении Like. Проверьте правильность " +
                "введенных данных");
    }

    public void removeLikeFilm(Long idFilm, Long idUser) {
        if (validatorFilmIdAndUserId(idFilm, idUser)) {
            if (userService.findUserById(idUser).getLikeFilmsId().contains(idFilm)) {
                userService.findUserById(idUser).getLikeFilmsId().remove(idFilm);
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
}


