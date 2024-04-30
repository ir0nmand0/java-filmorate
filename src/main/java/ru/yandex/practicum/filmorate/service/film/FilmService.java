package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    void addLike(final long id, final long userId);

    void removeLike(final long id, final long userId);

    Collection<Film> findAllLike(final int count);
}
