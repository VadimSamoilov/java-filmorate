package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundExeption;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.DbFilmsStorage;
import ru.yandex.practicum.filmorate.storage.film.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.film.DbMpaStorage;
import ru.yandex.practicum.filmorate.storage.user.DbLikeStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
@Slf4j

public class FilmService {

     // private final FilmStorage memoryFilmStorage;
     private final UserService userService;
    private final DbFilmsStorage dbFilmStorage;
    private final DbGenreStorage dbGenreStorage;
    private final DbMpaStorage dbMpaStorage;
    private final DbLikeStorage dbLikeStorage;

    public static final LocalDate RELISE = LocalDate.of(1895, Month.DECEMBER, 28);

    @Autowired
    public FilmService(UserService userService, DbFilmsStorage dbFilmStorage, DbGenreStorage dbGenreStorage, DbMpaStorage dbMpaStorage, DbLikeStorage dbLikeStorage) {
        this.userService = userService;
        this.dbFilmStorage = dbFilmStorage;
        this.dbGenreStorage = dbGenreStorage;
        this.dbMpaStorage = dbMpaStorage;
        this.dbLikeStorage = dbLikeStorage;
    }

    //вывод всех фильмов из базы
    public List<Film> findAllFilmsInStorage() {
        List<Film> films = dbFilmStorage.getFilmBase();
        return films;
    }

    // создание нового фильма
    public Film createNewFilm(Film film) {
        validateFilms(film);

        return dbFilmStorage.create(film);
    }

    // обновление информации о фильме
    public Film updateFilm(Film film) {
        final Film film1 = findFilmById(film.getFilm_id());
        validateFilms(film);
        film.setRate(film1.getRate());
        return dbFilmStorage.update(film);
    }

    // удаление фильма по ID
    public void deleteFilm(Long idFilm) {
        dbFilmStorage.delete(findFilmById(idFilm));

    }

    // поиск фильма по ID
    public Film findFilmById(Long idFilm) {
        return dbFilmStorage.getFilm(idFilm)
                .orElseThrow(() -> new FilmNotFoundExeption("Ошибка. Фильм с данным ID не найден"));
    }

    // повышаем рейтинг фильма - ставим лайк
    public void addLikeFilm(Long idFilm, Long idUser) {
        findFilmById(idFilm);
        userService.findUserById(idUser);
        dbLikeStorage.addLike(idUser,idFilm);
    }

    public void removeLikeFilm(Long idFilm, Long idUser) {
        findFilmById(idFilm);
        userService.findUserById(idUser);
        dbLikeStorage.removeLike(idFilm,idUser);

    }

    //???
    public List<Film> findPopularFilm(Integer count) {
        List<Film> films =dbLikeStorage.getPopular(count);
        return films;
    }

    // проверка на валидность переданного фильма
    private void validateFilms(Film film) {
        if (film.getReleaseDate().isBefore(RELISE)) {
            throw new CustomValidationException("\"Ошибка при добавлении фильма. Релиз фильма \" +\n" +
                    "                        \"не может быть раньше \" + RELISE)");

        }
    }
}


