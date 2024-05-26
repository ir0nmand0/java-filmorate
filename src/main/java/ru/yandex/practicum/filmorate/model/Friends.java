package ru.yandex.practicum.filmorate.model;

public record Friends(
        long id,
        long idFriend,
        boolean reciprocity) {
}
