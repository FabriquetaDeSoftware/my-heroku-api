-- This SQL script create schema
create schema if not exists spring;

-- This SQL script creates the users table
create table if not exists spring.users (
    id bigserial primary key,
    public_id varchar(255) not null unique,
    name varchar(100) not null,
    email varchar(255) not null unique,
    password text not null
);