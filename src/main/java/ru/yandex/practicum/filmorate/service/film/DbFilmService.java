package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DbFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public void addLike(final long id, final long userId) {
        filmStorage.findOrElseThrow(id);
        userStorage.findOrElseThrow(userId);
        filmStorage.addLikeInFavoriteFilmsOrElseThrow(id, userId);
    }

    @Override
    public void removeLike(final long id, final long userId) {
        filmStorage.findOrElseThrow(id);
        userStorage.findOrElseThrow(userId);
        filmStorage.removeLikeFromFavoriteFilmsOrElseThrow(id, userId);
    }

    @Override
    public Collection<Film> findAllLike(final int count) {
        return filmStorage.findAllLike(count);
    }
}
