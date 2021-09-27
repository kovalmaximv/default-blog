CREATE TABLE posts(
    id serial primary key,
    content text
);

CREATE TABLE user_roles(
    id serial primary key,
    name varchar(40) not null unique
);

CREATE TABLE users(
    id serial primary key,
    login varchar(50) not null unique,
    password varchar(150) not null,
    refresh_token varchar(500),
    role_id bigint not null references user_roles
);