package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;
import ru.yandex.practicum.filmorate.storage.film.DbFilmsStorage;
import ru.yandex.practicum.filmorate.storage.user.DBFriendsStorage;



import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase

class FilmorateApplicationTests {
    private final UserStorage userStorage;
    private final DbFilmsStorage filmStorage;
    private final DBFriendsStorage friendsStorage;

    @Autowired
    public FilmorateApplicationTests(UserStorage userStorage,
                                     DbFilmsStorage filmStorage, DBFriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.friendsStorage = friendsStorage;
    }

    private  final User user = new User(1,"warrior", "Андрей", "river@mail.ru",
            LocalDate.parse("1991-09-27"));
    private final User friend = new User(2,"Lordi", "Морди", "olimp@mail.ru",
            LocalDate.parse("1997-07-21"));
    private final User updateUser = new User(3,"Logan", "Новое имя Валеры", "vadim@mail.ru",
            LocalDate.parse("1988-02-15"));

    private final MPA mpa = new MPA(1,"g");

    private final Film film = new Film(1, "Killer", "Большое описание", LocalDate.parse("1990-07-11"),240, mpa,4);
    private final Film film2 = new Film(2,"Kill Bill", "Описание", LocalDate.parse("1989-07-11"), 180, mpa,4);

    @BeforeEach
    public void clear() {

    }

    @Test
    public void testCreateUser() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.save(user));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "UserLogin")
                );
    }


    @Test
    public void testFindUserById() throws SQLException {
        userStorage.save(user);
        Optional<User> userOptionalT = userStorage.getUser(user.getId());

        assertThat(userOptionalT)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "UserLogin")
                );
    }


    @Test
    public void testDeleteUserById() throws SQLException {
        userStorage.save(user);
        userStorage.save(friend);
        userStorage.deleteUser(user.getId());

        assertEquals(1, userStorage.getUsersBase().size());
    }

    @Test
    public void testUpdateUser() throws SQLException {
        userStorage.save(user);
        updateUser.setId(user.getId());
        userStorage.updateUser(updateUser);
        Optional<User> userOptional = userStorage.getUser(updateUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "UpdateLogin")
                );
    }

    @Test
    public void testAddAndShowFriend() {
        userStorage.save(user);
        userStorage.save(friend);
        friendsStorage.addFriend(user.getId(), friend.getId());
        Set<User> fl = new HashSet<>(friendsStorage.getFriends(user.getId()));
        assertEquals(1, fl.size());
        User friendUser = new User();

        for (User u : fl) {
            friendUser = u;
        }

        assertEquals("FriendLogin", friendUser.getLogin());
    }

    @Test
    public void testDeleteFriend() {
        userStorage.save(user);
        userStorage.save(friend);
        friendsStorage.addFriend(user.getId(), friend.getId());
        friendsStorage.removeFriend(user.getId(), friend.getId());

        assertEquals(0, friendsStorage.getFriends(user.getId()).size());
    }

    @Test
    public void testCreateFilm() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.save(film));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "Film Name")
                );
    }


    @Test
    public void testFindFilmById() {
        filmStorage.save(film);
        Optional<Film> filmOptional = filmStorage.getFilm(film.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "Film Name")
                );
    }

    @Test
    public void testReturnAllFilms() {
        filmStorage.save(film);
        filmStorage.save(film2);

        assertEquals(2, filmStorage.getFilmBase().size());
    }

    @Test
    public void testDeleteFilmById() {
        filmStorage.save(film);
        filmStorage.save(film2);
        filmStorage.delete(film2);

        assertEquals(1, filmStorage.getFilmBase().size());
    }

    @Test
    public void testUpdateFilm() {
        filmStorage.save(film);
        film2.setFilm_id(film.getId());
        filmStorage.save(film2);
        Optional<Film> filmOptional = filmStorage.getFilm(film2.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "Film Name2")
                );
    }
}

