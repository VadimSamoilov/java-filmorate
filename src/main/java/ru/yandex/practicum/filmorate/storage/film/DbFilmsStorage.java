package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundExeption;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.dao.FilmsStorage;
import ru.yandex.practicum.filmorate.storage.user.DbLikeStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component

public class DbFilmsStorage implements FilmsStorage {
    private Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private  MpaService mpaService;
    private GenreService genreService;
    private DbLikeStorage likeStorage;

    String CREATE_FILM_QUERY = "insert into FILMS(NAME, description, releaseDate, duration,genre,mpa) " +
            "values (?, ?, ?, ?, ?, ?)";
    String FIND_FILM_QUERY = "SELECT * FROM FILMS";
    String FILM_ID_QUERY = "select * from FILMS where FILM_ID = ?";

    @Autowired
    public DbFilmsStorage(JdbcTemplate jdbcTemplate, MpaService mpaService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaService = mpaService;
    }

    public Film save(Film film) {
        log.info(film.toString());
        if (film.getId() == null) {

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(CREATE_FILM_QUERY, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getGenre(), film.getMpa(), keyHolder);
            long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            film.setFilm_id(id);
            log.debug("Created user: {} with id: {}", film.getName(), id);
            return film;
        } else {
            String UPDATE_FILM_QUERY = "UPDATE FILMS " +
                    "SET NAME = ? " +
                    ", DESCRIPTION =  ?" +
                    ", RELEASEDATE = ?" +
                    ", DURATION = ?" +
                    ", GENRE = ?" +
                    ", MPA = ?" +
                    " WHERE ID = ?";

            if (jdbcTemplate.update(UPDATE_FILM_QUERY,
                    film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                    film.getGenre(), film.getMpa(),
                    Math.toIntExact(film.getId())) == 1) {
                return film;
            } else {
                throw new CustomValidationException("No user with such ID: " + film.getId());
            }
        }
    }

    @Override
    public void delete(Film film) {
        Film films = getFilm(film.getId()).get();
        String sqlQuery = "DELETE FROM films WHERE id = ? ";
        if (jdbcTemplate.update(sqlQuery, film.getId()) == 0) {
            throw new FilmNotFoundExeption("Фильм с ID=" + film.getId() + " не найден!");
        }
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        // выполняем запрос к БД
        if (id == null) {
            throw new CustomValidationException("Передан пустой аргумент!");
        }
        Film film;
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id = ?", id);
        if (filmRows.first()) {
            MPA mpa = mpaService.getMpaById(filmRows.getLong("MPA"));
            Collection<Genre> genres = genreService.getGenre();
            film = new Film(
                    filmRows.getLong("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    new HashSet<>(likeStorage.getLikes(filmRows.getLong("id"))),
                    mpa,
                    genres);
        } else {
            throw new FilmNotFoundExeption("Фильм с ID=" + id + " не найден!");
        }
        if (film.getGenre().isEmpty()) {
            film.setGenre(null);
        }
        return Optional.of(film);
    }

    @Override
    public List<Film> getFilmBase() {

        return jdbcTemplate.query(FIND_FILM_QUERY, DbFilmsStorage::makeFilm);
    }

    @Override
    public void saveGenre(Film film) {

        final Long filmId = film.getId();
        jdbcTemplate.update("delete from FILMGENRES where GENRE_FILM_ID = ?", filmId);
        final Set<Genre> genres = (Set<Genre>) film.getGenre();
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
                rs.getLong("id"),
                rs.getString("NAME"),
                rs.getString("DESCRIPTION"),
                Objects.requireNonNull(rs.getDate("RELEASEDATE")).toLocalDate(),
        rs.getDouble("DURATION"),
                rs.getInt("RATE"),
                (MPA) rs.getObject("MPA")
        );
    }
}
