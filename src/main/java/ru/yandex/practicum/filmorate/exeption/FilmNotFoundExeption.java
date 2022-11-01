package ru.yandex.practicum.filmorate.exeption;

import java.util.function.Supplier;

public class FilmNotFoundExeption extends RuntimeException {
    public FilmNotFoundExeption(String message) {
        super(message);
    }
}
