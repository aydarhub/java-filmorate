CREATE TABLE IF NOT EXISTS friendship (
	friendship_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	user1_id BIGINT NOT NULL,
	user2_id BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS users (
	user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	email VARCHAR,
	login VARCHAR NOT NULL,
	name VARCHAR NOT NULL,
	birthday DATE NOT NULL
);
CREATE TABLE IF NOT EXISTS likes (
	like_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	film_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS film (
	film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL,
	description VARCHAR(200),
	release_date DATE,
	duration INTEGER,
	mpa_id BIGINT
);
CREATE TABLE IF NOT EXISTS film_genre (
	film_genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	film_id BIGINT NOT NULL,
	genre_id INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS genre (
	genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL
);
CREATE TABLE IF NOT EXISTS mpa (
	mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL
);







