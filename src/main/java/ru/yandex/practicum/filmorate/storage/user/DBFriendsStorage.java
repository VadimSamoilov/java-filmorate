package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DBFriendsStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    public DBFriendsStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQuery = "merge into friends(USER_ID,FRIENDS_ID) values (?,?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sqlQuery = "delete from friends where user_id =? and friends_id =?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        final String sqlQuery = "SELECT FRIENDS_ID, email, login, name, birthday FROM friends" +
                " INNER JOIN users ON friends.FRIENDS_ID = users.ID WHERE friends.user_id = ?";
        return jdbcTemplate.query(sqlQuery,new BeanPropertyRowMapper<>(User.class),userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {
        final String sqlQuery = "select * from users u, friends f, friends o " +
                "where u.id = f.friends_id and u.id = o.friends_id " +
                "and f.user_id =? and o.user_id = ?";
        return jdbcTemplate.query(sqlQuery,new BeanPropertyRowMapper<>(User.class), new Object[]{userId, friendId});
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
