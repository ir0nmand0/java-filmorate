package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(final Film film) {
        if (isDuplicated(film)) {
            throw new ConditionsNotMetException("Фильм уже добавлен");
        }

        if (Objects.nonNull(film.getId())) {
            throw new ConditionsNotMetException("Id не допустим при создании фильма");
        }

        film.searchByFreeId(films);
        films.put(film.getId(), film);
        log.info(String.format("Фильм: %s добавлен в БД", film));
        return film;
    }

    @Override
    public Film update(final Film film) {
        findOrElseThrow(film.getId());
        films.put(film.getId(), film);
        log.info(String.format("Фильм: %s обновлен в БД", film));
        return film;
    }

    @Override
    public Film findOrElseThrow(final long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        return films.get(id);
    }

    private boolean isDuplicated(final Film film) {
        return films.values()
                .stream()
                .map(Film::getName)
                .anyMatch(filmName -> filmName.replaceAll("\\s", "")
                        .compareToIgnoreCase(film.getName().replaceAll("\\s", "")) == 0);
    }
}
