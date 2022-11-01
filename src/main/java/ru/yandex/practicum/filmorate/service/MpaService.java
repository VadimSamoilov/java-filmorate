package ru.yandex.practicum.filmorate.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

@Component
@Slf4j
public class MpaService {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_FROM_MPA_RATING = "SELECT * FROM MPA";
    private static final String SELECT_NAME_FROM_MPA_RATING_WHERE_MPA_ID =
            "SELECT TITLE_MPA FROM MPA WHERE MPA_ID = ?";

    public MpaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<MPA> getMpa() {
        return jdbcTemplate.query(SELECT_FROM_MPA_RATING, (rs, rowNum) -> new MPA(
                rs.getInt("mpa_id"),
                rs.getString("name"))
        );
    }

    public MPA get(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_NAME_FROM_MPA_RATING_WHERE_MPA_ID, id);
        if (userRows.next()) {
            MPA mpa = new MPA(
                    id,
                    userRows.getString("name")
            );
            log.info("Mpa found: {}", mpa);
            return mpa;
        } else throw new CustomValidationException(String.format("Mpa not found: id=%d", id));
    }
}