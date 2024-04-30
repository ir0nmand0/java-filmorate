package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(final Film film);

    Film update(final Film film);

    Film findOrElseThrow(final long id);

    void addLike(long id, long userId);

    void removeLike(long id, long userId);

    Collection<Film> findAllLike(int count);
}
