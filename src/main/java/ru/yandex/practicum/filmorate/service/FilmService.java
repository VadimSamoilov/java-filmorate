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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage memoryFilmStorage;
    private final UserService userService;
    private Long id = 0L;
    public static final LocalDate RELISE = LocalDate.of(1895, Month.DECEMBER, 28);

    //вывод всех фильмов из базы
    public List<Film> findAllFilmsInStorage() {
        return memoryFilmStorage.getFilmBase();
    }

    // создание нового фильма
    public Film createNewFilm(Film film) {
        film.setId(++id);
        validateFilms(film);
        return memoryFilmStorage.create(film);
    }

    // обновление информации о фильме
    public Film updateFilm(Film film) {
        validateFilms(film);
        validatorPresenceOfTheMovieITheStorage(film);
        id--;
        return memoryFilmStorage.update(film);
    }

    // удаление фильма по ID
    public void deleteFilm(Long idFilm) {
        validatorPresenceOfTheMovieITheStorage(findFilmById(idFilm));
        memoryFilmStorage.delete(findFilmById(idFilm));
    }

    // поиск фильма по ID
    public Film findFilmById(Long idFilm) {
        return Optional.ofNullable(idFilm).filter(p -> p > 0)
                .map(memoryFilmStorage::getFilm)
                .orElseThrow(() -> new FilmNotFoundExeption("Фильм с данным ID не найден"));
    }

    // повышаем рейтинг фильма - ставим лайк
    public void addLikeFilm(Long idFilm, Long idUser) {
        validatorFilmIdAndUserId(idFilm, idUser);
        findFilmById(idFilm).addlikeFilmUser(idUser);
    }

    public void removeLikeFilm(Long idFilm, Long idUser) {
        validatorFilmIdAndUserId(idFilm, idUser);
            if (findFilmById(idFilm).getLikeFilmsIdUser().contains(idUser)) {
                findFilmById(idFilm).removeLike(idUser);
            } else throw new FilmNotFoundExeption("Фильм не найдет в избранном у пользователя");
    }

    public List<Film> findPopularFilm(Integer count) {
        return memoryFilmStorage.findPopularFilm(count);
    }

    private void validatorFilmIdAndUserId(Long idFilm, Long idUser) {
        Optional.ofNullable(idFilm).filter(p -> (p > 0) && (p != null))
                .orElseThrow(() -> new FilmNotFoundExeption("Невозможно найти пользователя и фильм"));
        Optional.ofNullable(idUser).filter(p -> (p > 0) && (p != null)).orElseThrow(() -> new UserNotFoundExeption("Неверный формат данных"));
        Optional.ofNullable(idFilm).filter(p -> memoryFilmStorage.getFilm(p) != null)
                .orElseThrow(() -> new FilmNotFoundExeption("Невозможно найти пользователя и фильм"));
        Optional.ofNullable(idUser).filter(p -> userService.findUserById(p) != null)
                .orElseThrow(() -> new UserNotFoundExeption("Неверный формат данных"));
    }


    // проверка на валидность переданного фильма
    private void validateFilms(Film film) {
        Optional.ofNullable(film).map(Film::getId).filter(p -> p >= 0)
                .orElseThrow(() -> new FilmNotFoundExeption("ID не может быть отрицателоьным."));
        Optional.ofNullable(film).map(Film::getReleaseDate).filter(p -> RELISE.isBefore(p))
                .orElseThrow(() -> new CustomValidationException("\"Ошибка при добавлении фильма. Релиз фильма \" +\n" +
                        "                        \"не может быть раньше \" + RELISE)"));

    }

    private void validatorPresenceOfTheMovieITheStorage(Film film) {
        Optional.ofNullable(film).map(Film::getId).filter(p -> findFilmById(p) != null)
                .orElseThrow(() -> new FilmNotFoundExeption("Ошибка валидации фильма"));
    }
}


