package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DbGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenre(long genreId) {
        final String sqlQuery = "select * from GENRE where genre_id = ?";
        final List<Genre> films = jdbcTemplate.query(sqlQuery, DbGenreStorage::mareGenre, genreId);
        if (films.size() != 1) {
            throw new CustomValidationException("genre id = " + genreId);
        }
        return films.get(0);
    }

    @Override
    public List<Genre> getAll() {
        return jdbcTemplate.query("select * from GENRE", DbGenreStorage::mareGenre);
    }

    // подумать над реализацией
//    @Override
//    public void load(List<Film> films) {
//    String inSql = String.join(",", Collections.nCopies(films.size(),"?"));
//    final Map<Long,Film> filmbyId = films.stream().collect(Collectors.toMap(Film::getFilm_id, Function.identity()));
//    final String sqlQuery = "select * from genre g, FILMGENRES fg where fg.genre_id = g.GENRE_ID and fg.GENRE_FILM_ID " +
//            "in (" +inSql + ")";
//    jdbcTemplate.query(sqlQuery,(rs -> {
//        final Film film = filmbyId.get(rs.getLong("GENRE_FILM_ID"));
//        film.addGenre(mareGenre(rs, 0));
//    }),
//            films.stream().map(Film::getFilm_id).toArray());
//    }
    static Genre mareGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getLong("genre_id"),
                rs.getString("name"));

    }

    @Override
    public void add(Film film) {
        if (film.getGenre() != null) {
            for (Genre genre : film.getGenre()) {
                jdbcTemplate.update("INSERT INTO FILMGENRES (GENRE_FILM_ID, genre_id) VALUES (?, ?)",
                        film.getFilm_id(), genre.getGenreId());
            }
        }
    }

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        String sql = "SELECT genre_id, TITLE_GENRE FROM FILMGENRES" +
                " INNER JOIN GENRE ON genre_id = GENRE.GENRE_ID WHERE GENRE_FILM_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"), rs.getString("name")), filmId
        );
    }


}
