package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Validated @RequestBody Film film) {
        if (!films.isEmpty()) {
            films.values()
                    .stream()
                    .map(Film::getName)
                    .filter(filmName -> filmName.replaceAll("\\s", "")
                            .compareToIgnoreCase(film.getName().replaceAll("\\s", "")) != 0)
                    .findFirst()
                    .orElseThrow(() -> new ConditionsNotMetException("Фильм уже добавлен"));
        }

        film.searchByFreeId(films);
        films.put(film.getId(), film);
        log.info(String.format("Фильм: %s добавлен в БД", film));
        return film;
    }

    @PutMapping
    public Film update(@Validated @RequestBody Film film) {
        if (isEmpty(film.getId())) {
            films.put(film.getId(), film);
            log.info(String.format("Фильм: %s обновлен в БД", film));
            return film;
        }

        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    private boolean isEmpty(final long id) {
        return films.containsKey(id);
    }
}
