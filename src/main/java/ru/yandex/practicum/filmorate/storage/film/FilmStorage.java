package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(final Film film);

    Film update(final Film film);

    void addLike(final long id, final long userId);

    void removeLike(final long id, final long userId);

    Collection<Film> findAllLike(final int count);
}
