package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(final Film film);

    Film update(final Film film);

    Film findOrElseThrow(final long id);

    Optional<Film> getFilm(final long id);

    Collection<Film> findAllLike(final int count);

    void addLikeInFavoriteFilmsOrElseThrow(final long id, final long userId);

    void removeLikeFromFavoriteFilmsOrElseThrow(long id, long userId);

    boolean isDuplicatedName(final Film film);
}
