package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import java.util.List;

@Component
public class DbMpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MPA> getAll() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MPA(
                rs.getInt("id"),
                rs.getString("name"))
        );
    }

    public MPA getMpaId(Long mpaId) {
        if (mpaId == null) {
            throw new CustomValidationException("Передан пустой аргумент!");
        }
        MPA mpa;
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM MPA WHERE id = ?", mpaId);
        if (mpaRows.first()) {
            mpa = new MPA(
                    mpaRows.getInt("id"),
                    mpaRows.getString("name")
            );
        } else {
            throw new CustomValidationException("Рейтинг с ID=" + mpaId + " не найден!");
        }
        return mpa;
    }

}
