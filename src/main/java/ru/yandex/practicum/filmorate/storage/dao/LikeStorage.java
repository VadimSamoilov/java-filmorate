package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikeStorage {

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId,Long userId);

    List<Film> getPopular(Integer count);
}
