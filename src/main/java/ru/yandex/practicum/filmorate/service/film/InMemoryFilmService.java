package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;

    @Override
    public void addLike(final long id, final long userId) {
        filmStorage.addLike(id, userId);
    }

    @Override
    public void removeLike(final long id, final long userId) {
        filmStorage.removeLike(id, userId);
    }

    @Override
    public Collection<Film> findAllLike(final int count) {
        return filmStorage.findAllLike(count);
    }
}
