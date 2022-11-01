package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DbMpaStorage implements MpaStorage {

    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;



    @Override
    public MPA getMpa(long id) {
        String sqlQuery = "select * from mpa where MPA_ID = ?";
        return jdbcTemplate.query(sqlQuery,DbMpaStorage::makeMpa,id).stream()
                .findAny().orElse(null);
    }

    @Override
    public List<MPA> getAll() {
        return jdbcTemplate.query("select * from mpa",DbMpaStorage::makeMpa);
    }

    static MPA makeMpa(ResultSet rs, int rowNum) throws SQLException{
        return new MPA(
                rs.getLong("MPA_ID"),
                rs.getString("TITLE_MPA")
        );
    }

}
