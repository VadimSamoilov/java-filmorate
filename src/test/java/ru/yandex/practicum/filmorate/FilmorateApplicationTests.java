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
import ru.yandex.practicum.filmorate.storage.inmemorydb.FilmStorage;
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
    private final FilmStorage filmStorage;
    private final DBFriendsStorage friendsStorage;

    @Autowired
    public FilmorateApplicationTests(UserStorage userStorage,
                                    FilmStorage filmStorage, DBFriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.friendsStorage = friendsStorage;
    }

    private  final User user = new User(1L,"warrior", "Андрей", "river@mail.ru",
            LocalDate.parse("1991-09-27"));
    private final User friend = new User(2L,"Lordi", "Морди", "olimp@mail.ru",
            LocalDate.parse("1997-07-21"));
    private final User updateUser = new User(3L,"Logan", "Имя Валеры", "vadim@mail.ru",
            LocalDate.parse("1988-02-15"));

    private final MPA mpa = new MPA(1,"g");

    private final Film film = new Film(1L, "Killer", "Большое описание", LocalDate.parse("1990-07-11"),240,  mpa);
    private final Film film2 = new Film(2L,"Kill Bill", "Описание", LocalDate.parse("1989-07-11"), 180, mpa);

    @BeforeEach
    public void clear() {

    }

    @Test
    public void testCreateUser() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.createUser(user));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "UserLogin")
                );
    }


    @Test
    public void testFindUserById() throws SQLException {
        userStorage.createUser(user);
        Optional<User> userOptionalT = userStorage.getUser(user.getUser_id());

        assertThat(userOptionalT)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "UserLogin")
                );
    }


    @Test
    public void testDeleteUserById() throws SQLException {
        userStorage.createUser(user);
        userStorage.createUser(friend);
        userStorage.deleteUser(user.getUser_id());

        assertEquals(1, userStorage.getUsersBase().size());
    }

    @Test
    public void testUpdateUser() throws SQLException {
        userStorage.createUser(user);
        updateUser.setUser_id(user.getUser_id());
        userStorage.updateUser(updateUser);
        Optional<User> userOptional = userStorage.getUser(updateUser.getUser_id());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "UpdateLogin")
                );
    }

    @Test
    public void testAddAndShowFriend() {
        userStorage.createUser(user);
        userStorage.createUser(friend);
        friendsStorage.addFriend(user.getUser_id(), friend.getUser_id());
        Set<User> fl = new HashSet<>(friendsStorage.getFriends(user.getUser_id()));
        assertEquals(1, fl.size());
        User friendUser = new User();

        for (User u : fl) {
            friendUser = u;
        }

        assertEquals("FriendLogin", friendUser.getLogin());
    }

    @Test
    public void testDeleteFriend() {
        userStorage.createUser(user);
        userStorage.createUser(friend);
        friendsStorage.addFriend(user.getUser_id(), friend.getUser_id());
        friendsStorage.removeFriend(user.getUser_id(), friend.getUser_id());

        assertEquals(0, friendsStorage.getFriends(user.getUser_id()).size());
    }

    @Test
    public void testCreateFilm() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.create(film));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "Film Name")
                );
    }


    @Test
    public void testFindFilmById() {
        filmStorage.create(film);
        Optional<Film> filmOptional = filmStorage.getFilm(film.getFilm_id());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "Film Name")
                );
    }

    @Test
    public void testReturnAllFilms() {
        filmStorage.create(film);
        filmStorage.create(film2);

        assertEquals(2, filmStorage.getFilmBase().size());
    }

    @Test
    public void testDeleteFilmById() {
        filmStorage.create(film);
        filmStorage.create(film2);
        filmStorage.delete(film2);

        assertEquals(1, filmStorage.getFilmBase().size());
    }

    @Test
    public void testUpdateFilm() {
        filmStorage.create(film);
        film2.setFilm_id(film.getFilm_id());
        filmStorage.update(film2);
        Optional<Film> filmOptional = filmStorage.getFilm(film2.getFilm_id());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "Film Name2")
                );
    }
}

