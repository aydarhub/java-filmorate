INSERT INTO PUBLIC.USERS
(EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES('user1@mail.ru', 'loginuser1', 'user1', '2000-06-15');

INSERT INTO PUBLIC.USERS
(EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES('user2@mail.ru', 'loginuser2', 'user2', '2000-05-13');

INSERT INTO PUBLIC.USERS
(EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES('user3@mail.ru', 'loginuser3', 'user3', '2001-03-25');

INSERT INTO PUBLIC.USERS
(EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES('user4@mail.ru', 'loginuser4', 'user4', '1999-06-15');

INSERT INTO PUBLIC.USERS
(EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES('user5@mail.ru', 'loginuser5', 'user5', '1992-06-15');

INSERT INTO PUBLIC.USERS
(EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES('user6@mail.ru', 'loginuser6', 'user6', '1995-06-15');

INSERT INTO PUBLIC.USERS
(EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES('user7@mail.ru', 'loginuser7', 'user7', '2000-04-16');

INSERT INTO PUBLIC.FRIENDSHIP
(USER1_ID, USER2_ID, STATUS)
VALUES(1, 2, 'FRIEND');

INSERT INTO PUBLIC.FRIENDSHIP
(USER1_ID, USER2_ID, STATUS)
VALUES(2, 3, 'FRIEND');

INSERT INTO PUBLIC.FRIENDSHIP
(USER1_ID, USER2_ID, STATUS)
VALUES(3, 1, 'FRIEND');

INSERT INTO PUBLIC.FRIENDSHIP
(USER1_ID, USER2_ID, STATUS)
VALUES(1, 4, 'NOT_APPROVED');

INSERT INTO PUBLIC.FRIENDSHIP
(USER1_ID, USER2_ID, STATUS)
VALUES(3, 5, 'NOT_APPROVED');

INSERT INTO PUBLIC.FRIENDSHIP
(USER1_ID, USER2_ID, STATUS)
VALUES(7, 1, 'NOT_APPROVED');

INSERT INTO PUBLIC.FRIENDSHIP
(USER1_ID, USER2_ID, STATUS)
VALUES(4, 7, 'DECLINED');

INSERT INTO PUBLIC.FRIENDSHIP
(USER1_ID, USER2_ID, STATUS)
VALUES(7, 2, 'DECLINED');

INSERT INTO PUBLIC.FRIENDSHIP
(USER1_ID, USER2_ID, STATUS)
VALUES(7, 3, 'DECLINED');

INSERT INTO PUBLIC.MPA
(NAME)
VALUES('G');

INSERT INTO PUBLIC.MPA
(NAME)
VALUES('PG');

INSERT INTO PUBLIC.MPA
(NAME)
VALUES('PG-13');

INSERT INTO PUBLIC.MPA
(NAME)
VALUES('R');

INSERT INTO PUBLIC.MPA
(NAME)
VALUES('NC-17');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Комедия');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Драма');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Мультфильм');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Триллер');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Документальный');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Боевик');

INSERT INTO PUBLIC.FILMS
(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES('Иван Царевич и Серый Волк 5', 'Одно беспечное путешествие в прошлое — и Ивана все забыли в настоящем. «Эффект бабочки» в Тридевятом царстве', '2022-12-29', 4680, 2);

INSERT INTO PUBLIC.FILMS
(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES('Всё везде и сразу', 'Судьба мультивселенной — в руках владелицы прачечной. Изобретательный и остроумный боевик с Мишель Йео', '2022-04-07', 8340, 5);

INSERT INTO PUBLIC.FILMS
(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES('Фокус', 'История об опытном мошеннике, который влюбляется в девушку, делающую первые шаги на поприще нелегального отъема средств у граждан.', '2015-02-24', 6240, 5);

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('фантастика');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('комедия');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('боевик');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('приключения');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('драма');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('мультфильм');

INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('семейный');

INSERT INTO PUBLIC.FILM_GENRE
(FILM_ID, GENRE_ID)
VALUES(1, 6);

INSERT INTO PUBLIC.FILM_GENRE
(FILM_ID, GENRE_ID)
VALUES(1, 4);

INSERT INTO PUBLIC.FILM_GENRE
(FILM_ID, GENRE_ID)
VALUES(1, 7);

INSERT INTO PUBLIC.FILM_GENRE
(FILM_ID, GENRE_ID)
VALUES(2, 1);

INSERT INTO PUBLIC.FILM_GENRE
(FILM_ID, GENRE_ID)
VALUES(2, 2);

INSERT INTO PUBLIC.FILM_GENRE
(FILM_ID, GENRE_ID)
VALUES(2, 3);

INSERT INTO PUBLIC.FILM_GENRE
(FILM_ID, GENRE_ID)
VALUES(2, 4);

INSERT INTO PUBLIC.FILM_GENRE
(FILM_ID, GENRE_ID)
VALUES(2, 5);

SELECT * FROM FILM_GENRE;

INSERT INTO PUBLIC.LIKES
(FILM_ID, USER_ID)
VALUES(1, 2);

INSERT INTO PUBLIC.LIKES
(FILM_ID, USER_ID)
VALUES(1, 1);


INSERT INTO PUBLIC.LIKES
(FILM_ID, USER_ID)
VALUES(2, 3);


INSERT INTO PUBLIC.LIKES
(FILM_ID, USER_ID)
VALUES(2, 4);


INSERT INTO PUBLIC.LIKES
(FILM_ID, USER_ID)
VALUES(2, 5);