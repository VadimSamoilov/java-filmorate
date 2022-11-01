package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("userDbStorage")

public class DbUserStorage implements ru.yandex.practicum.filmorate.storage.dao.UserStorage {
    String CREATE_USER_QUERY = "insert into USERS(name, email, login, birth_day) " +
            "values (?, ?, ?,?)";
    String FIND_USERS_QUERY = "SELECT * FROM USERS";
    String USER_ID_QUERY = "select * from USERS where USER_ID = ?";


    private Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> getUser(long id) {
        // выполняем запрос к БД
        return Optional.ofNullable(jdbcTemplate.query(USER_ID_QUERY, DbUserStorage::makeUser, id)
                .stream().findAny().orElse(null));
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(CREATE_USER_QUERY, user.getName(), user.getEmail(),
                user.getLogin(), Date.valueOf(user.getBirth_day()), keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setUser_id(id);
        log.debug("Created user: {} with id: {}", user.getName(), id);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        String DELETE_USER_QUERY = "DELETE FROM USERS" +
                " WHERE USER_ID = ?";
        jdbcTemplate.update(DELETE_USER_QUERY, id);
    }

    @Override
    public User updateUser(User user) {
        String UPDATE_USER_QUERY = "UPDATE USERS " +
                "SET name = ? " +
                ", email =  ?" +
                ", login = ?" +
                ", BIRTH_DAY = ?" +
                " WHERE USER_ID = ?";

        if (jdbcTemplate.update(UPDATE_USER_QUERY,
                user.getName(), user.getEmail(), user.getLogin(), user.getBirth_day(),
                Math.toIntExact(user.getUser_id())
        ) == 1) {
            return user;
        } else {
            throw new CustomValidationException("No user with such ID: " + user.getUser_id());
        }
    }

    @Override
    public List<User> getUsersBase() {
        return jdbcTemplate.query(FIND_USERS_QUERY, DbUserStorage::makeUser);
    }


    static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getLong("user_id"),rs.getString("user_login"),
                rs.getString("user_name"),rs.getString("user_email"),
                Objects.requireNonNull(rs.getDate("BIRTH_DAY")).toLocalDate());
    }

}
