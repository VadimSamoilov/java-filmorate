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
import java.util.List;



@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage memoryFilmStorage;
    private final UserService userService;

    public static final LocalDate RELISE = LocalDate.of(1895, Month.DECEMBER, 28);

    //вывод всех фильмов из базы
    public List<Film> findAllFilmsInStorage() {
        return memoryFilmStorage.getFilmBase();
    }

    // создание нового фильма
    public Film createNewFilm(Film film) {
        validateFilms(film);
        return memoryFilmStorage.create(film);
    }

    // обновление информации о фильме
    public Film updateFilm(Film film) {
        validateFilms(film);
        findFilmById(film.getFilm_id());
        return memoryFilmStorage.update(film);
    }

    // удаление фильма по ID
    public void deleteFilm(Long idFilm) {
        findFilmById(idFilm);

        memoryFilmStorage.delete(findFilmById(idFilm));
    }

    // поиск фильма по ID
    public Film findFilmById(Long idFilm) {
        return memoryFilmStorage.getFilm(idFilm)
                .orElseThrow(() -> new FilmNotFoundExeption("Ошибка. Фильм с данным ID не найден"));
    }

    // повышаем рейтинг фильма - ставим лайк
    public void addLikeFilm(Long idFilm, Long idUser) {
        userService.findUserById(idUser);
        (memoryFilmStorage.getFilm(idFilm).get()).addlikeFilmUser(idUser);
        log.info(findFilmById(idFilm).toString());
    }

    public void removeLikeFilm(Long idFilm, Long idUser) {
        if (findFilmById(idFilm).getLikeFilmsIdUser().contains(idUser)) {
            findFilmById(idFilm).removeLike(idUser);
        } else throw new FilmNotFoundExeption("Фильм не найдет в избранном у пользователя");
    }

    public List<Film> findPopularFilm(Integer count) {
        return memoryFilmStorage.findPopularFilm(count);
    }

    // проверка на валидность переданного фильма
    private void validateFilms(Film film) {
        if (film.getReleaseDate().isBefore(RELISE)) {
            throw new CustomValidationException("\"Ошибка при добавлении фильма. Релиз фильма \" +\n" +
                    "                        \"не может быть раньше \" + RELISE)");

        }
    }
}


