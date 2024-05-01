package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(final Film film);

    Film update(final Film film);

    Film findOrElseThrow(final long id);

    Collection<Film> findAllLike(final int count);

    void ifEmptyThenPutInUserLikes(final long id);

    int getSizeUserLikes(final long id);

    void addLikeInUserLikesOrElseThrow(final long id, final long userId);

    void addLikeInNumberOfLikesOrElseThrow(final int size, final long id, final long userId);

    void transferLikesInInNumberOfLikes(final int size, final int oldSize);

    void removeLikeFromNumberOfLikes(final int size, final long id);

    void removeLikeFromUserLikesOrElseThrow(long id, long userId);

    void ifEmptyThenPutInNumberOfLikes(int id);
}
