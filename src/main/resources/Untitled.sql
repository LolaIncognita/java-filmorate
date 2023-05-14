CREATE TABLE "film" (
  "id" integer PRIMARY KEY,
  "name" varchar,
  "description" varchar,
  "relizeDate" timestamp,
  "duration" integer,
  "mpa_id" integer
);

CREATE TABLE "user" (
  "id" integer PRIMARY KEY,
  "email" varchar,
  "login" varchar,
  "name" varchar,
  "birthday" timestamp
);

CREATE TABLE "user_friends" (
  "user_id" integer,
  "friend_id" integer,
  "status" boolean
);

CREATE TABLE "likes" (
  "film_id" integer,
  "user_id" integer
);

CREATE TABLE "film_genre" (
  "film_id" integer,
  "genre_id" integer
);

CREATE TABLE "genre" (
  "id" integer PRIMARY KEY,
  "name" varchar
);

CREATE TABLE "mpa" (
  "id" integer PRIMARY KEY,
  "name" varchar
);

ALTER TABLE "user_friends" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "user" ADD FOREIGN KEY ("id") REFERENCES "user_friends" ("friend_id");

ALTER TABLE "mpa" ADD FOREIGN KEY ("id") REFERENCES "film" ("mpa_id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("id");

ALTER TABLE "genre" ADD FOREIGN KEY ("id") REFERENCES "film_genre" ("ganre_id");

ALTER TABLE "likes" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("id");
