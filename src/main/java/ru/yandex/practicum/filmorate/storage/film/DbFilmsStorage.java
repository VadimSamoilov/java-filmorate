package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dao.FilmsStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component

public class DbFilmsStorage implements FilmsStorage {
    private Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    String CREATE_FILM_QUERY = "insert into FILMS(TITLE, description, releaseDate, duration,genre,mpa) " +
            "values (?, ?, ?, ?, ?, ?)";
    String FIND_FILM_QUERY = "SELECT * FROM FILMS";
    String FILM_ID_QUERY = "select * from FILMS where FILM_ID = ?";

    @Autowired
    public DbFilmsStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(CREATE_FILM_QUERY, film.getTitle(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getGenre(), film.getMpa(), keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setFilm_id(id);
        log.debug("Created user: {} with id: {}", film.getTitle(), id);
        return film;

    }

    @Override
    public void delete(Film film) {
        String DELETE_USER_QUERY = "DELETE FROM FILMS" +
                " WHERE FILM_ID = ?";
        jdbcTemplate.update(DELETE_USER_QUERY, film.getFilm_id());
    }

    @Override
    public Film update(Film film) {
        String UPDATE_FILM_QUERY = "UPDATE FILMS " +
                "SET TITLE = ? " +
                ", DESCRIPTION =  ?" +
                ", RELEASEDATE = ?" +
                ", DURATION = ?" +
                ", GENRE = ?" +
                ", MPA = ?" +
                " WHERE FILM_ID = ?";

        if (jdbcTemplate.update(UPDATE_FILM_QUERY,
                film.getTitle(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getGenre(), film.getMpa(),
                Math.toIntExact(film.getFilm_id())) == 1) {
            return film;
        } else {
            throw new CustomValidationException("No user with such ID: " + film.getFilm_id());
        }
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        return Optional.ofNullable(jdbcTemplate.query(FILM_ID_QUERY, DbFilmsStorage::makeFilm,
                id).stream().findAny().orElse(null));
    }


    @Override
    public List<Film> getFilmBase() {

        return jdbcTemplate.query(FIND_FILM_QUERY, new BeanPropertyRowMapper<>(Film.class));
    }

    @Override
    public void saveGenre(Film film) {

        final Long filmId = film.getFilm_id();
        jdbcTemplate.update("delete from FILMGENRES where GENRE_FILM_ID = ?", filmId);
        final Set<Genre> genres = film.getGenre();
        if (genres == null || genres.isEmpty()) {
            return;
        }
        final ArrayList<Genre> genreList = new ArrayList<>(genres);
        jdbcTemplate.batchUpdate("" +
                        "insert  into FILMGENRES (genre_film_id, genre_id) values (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genreList.get(i).getGenreId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genreList.size();
                    }
                });
    }

    static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("film_id"),
                rs.getString("TITLE"),
                rs.getString("DESCRIPTION"),
                Objects.requireNonNull(rs.getDate("RELEASEDATE")).toLocalDate(),
        rs.getDouble("DURATION"),
                rs.getInt("RATE"),
                (MPA) rs.getObject("MPA_ID ")
        );
    }
}
