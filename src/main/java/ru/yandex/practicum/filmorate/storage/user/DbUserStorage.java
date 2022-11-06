package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.CustomValidationException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundExeption;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("userDbStorage")

public class DbUserStorage implements ru.yandex.practicum.filmorate.storage.dao.UserStorage {
    final String CREATE_USER_QUERY = "insert into USERS(login,name, email, BIRTHDAY) " +
            "values (?, ?, ?,?)";
    final String FIND_USERS_QUERY = "SELECT * FROM USERS";
    final String USER_ID_QUERY = "SELECT ID, LOGIN, NAME, EMAIL, BIRTHDAY FROM USERS WHERE user_id = ?";
    final String UPDATE_USER = "UPDATE USERS SET login = ?,name = ?,  email = ?, birthday = ? WHERE id = ?";


    private Logger log = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> getUser(Long id) {
        // выполняем запрос к БД
        if (id == null) {
            throw new CustomValidationException("Передан пустой аргумент!");
        }
        User user;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE ID = ?", id);
        if (userRows.first()) {
            user = new User(
                    userRows.getInt("ID"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
        } else {
            throw new UserNotFoundExeption("Пользователь с ID=" + id + " не найден!");
        }
        return Optional.of(user);
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(CREATE_USER_QUERY,
                        new String[]{"id"});

                ps.setString(1, user.getLogin());
                ps.setString(2, user.getName());
                ps.setString(3, user.getEmail());
                ps.setDate(4, Date.valueOf(user.getBirthday()));
                return ps;
            }, keyHolder);
            Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            user.setId(id);
            log.debug("Created user: {} with id: {}", user.getName(), id);
            return user;
        } else {
            jdbcTemplate.update(UPDATE_USER,  user.getLogin(),
                    user.getName(),user.getEmail(), Date.valueOf(user.getBirthday()), user.getId());
            return user;
        }
    }

    @Override
    public void deleteUser(Long id) {
        String DELETE_USER_QUERY = "DELETE FROM USERS" +
                " WHERE ID = ?";
        jdbcTemplate.update(DELETE_USER_QUERY, id);
    }

    @Override
    public User updateUser(User user) {
        final String UPDATE_USER =
                "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

        log.info(user.toString());
        if ((jdbcTemplate.update(UPDATE_USER, user.getEmail(), user.getLogin(),
                user.getName(), Date.valueOf(user.getBirthday()), user.getId())) == 1) {
            return user;
        } else {
            throw new CustomValidationException("No user with such ID: " + user.getId());
        }
    }

    @Override
    public List<User> getUsersBase() {
        return jdbcTemplate.query(FIND_USERS_QUERY, DbUserStorage::makeUser);
    }


    static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getInt("ID"),
                rs.getString("LOGIN"),
                rs.getString("NAME"),
                rs.getString("EMAIL"),
                rs.getDate("BIRTHDAY").toLocalDate());
    }

}
