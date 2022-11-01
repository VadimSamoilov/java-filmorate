package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import java.util.List;

@Component
public class DbLikeStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    public DbLikeStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long userId, long filmId) {
        String sqlQuery = "merge into liketables(USER_ID,FILM_ID) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void updateRate(long filmId) {
String sqlQuery = "update films f set rate = (select count (LIKETABLES.USER_ID) from LIKETABLES " +
        "where  LIKETABLES.FILM_ID= f.film_id) where f.film_id =?";
jdbcTemplate.update(sqlQuery,filmId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sqlQuery = "delete from liketables where film_id =? and user_id =?";
        jdbcTemplate.update(sqlQuery,filmId,userId);
        updateRate(filmId);
    }

    @Override
    public List<Film> getPopular(int count) {
        return jdbcTemplate.query("select * from FILMS, " +
                "MPA m where Films.MPA order by rate desc limit ?", new BeanPropertyRowMapper<>(Film.class),count);
    }
}
