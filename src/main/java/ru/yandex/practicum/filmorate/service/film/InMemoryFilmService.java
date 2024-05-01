package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public void addLike(final long id, final long userId) {
        filmStorage.findOrElseThrow(id);
        userStorage.findOrElseThrow(userId);
        filmStorage.ifEmptyThenPutInUserLikes(id);
        final int oldSize = filmStorage.getSizeUserLikes(id);
        filmStorage.ifEmptyThenPutInNumberOfLikes(oldSize);
        filmStorage.addLikeInUserLikesOrElseThrow(id, userId);
        filmStorage.removeLikeFromNumberOfLikes(oldSize, id);
        final int size = filmStorage.getSizeUserLikes(id);
        filmStorage.ifEmptyThenPutInNumberOfLikes(size);
        filmStorage.addLikeInNumberOfLikesOrElseThrow(size, id, userId);
    }

    @Override
    public void removeLike(final long id, final long userId) {
        filmStorage.findOrElseThrow(id);
        userStorage.findOrElseThrow(userId);
        final int oldSize = filmStorage.getSizeUserLikes(id);
        filmStorage.ifEmptyThenPutInNumberOfLikes(oldSize);
        filmStorage.removeLikeFromUserLikesOrElseThrow(id, userId);
        filmStorage.removeLikeFromNumberOfLikes(oldSize, id);
        final int size = filmStorage.getSizeUserLikes(id);
        filmStorage.ifEmptyThenPutInNumberOfLikes(size);
        filmStorage.transferLikesInInNumberOfLikes(size, oldSize);
    }

    @Override
    public Collection<Film> findAllLike(final int count) {
        return filmStorage.findAllLike(count);
    }
}
