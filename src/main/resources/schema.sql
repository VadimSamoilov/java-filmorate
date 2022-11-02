create table if not exists USERS
(
    USER_ID   INTEGER primary key auto_increment,
    NAME      CHARACTER VARYING(50),
    EMAIL     CHARACTER VARYING(100)      not null,
    LOGIN     CHARACTER VARYING(100)      not null,
    BIRTH_DAY DATE,
    deleted bool default false,
    UNIQUE (EMAIL),
    UNIQUE (LOGIN)
);

create table if not exists FRIENDS
(
    USER_ID    INTEGER not null references USERS(USER_ID),
    FRIENDS_ID INTEGER not null references USERS(USER_ID),
    primary key (USER_ID ,FRIENDS_ID)
);



create table if not exists FILMS
(
    FILM_ID     INTEGER not null primary key auto_increment,
    TITLE       CHARACTER VARYING(150),
    DESCRIPTION CHARACTER VARYING(150),
    RELEASEDATE DATE,
    DURATION    REAL,
    RATE        INTEGER,
    GENRE       INTEGER ,
    MPA         INTEGER references MPA (MPA_ID)
);


create table if not exists FILMGENRES
(
    GENRE_FILM_ID INTEGER references FILMS (FILM_ID),
    GENRE_ID      INTEGER references GENRE (GENRE_ID),
    primary key (GENRE_FILM_ID, GENRE_ID)
);

create table if not exists GENRE
(
    GENRE_ID    INTEGER primary key auto_increment,
    TITLE_GENRE CHARACTER VARYING(150) not null

);
create table if not exists MPA
(
    MPA_ID    INTEGER primary key auto_increment,
    TITLE_MPA CHARACTER VARYING(50) not null

);
