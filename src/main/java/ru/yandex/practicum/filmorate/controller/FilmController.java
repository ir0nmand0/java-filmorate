package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping(value = "/popular")
    public Collection<Film> findAllLike(@RequestParam(required = false, defaultValue = "10") final int count) {
        return filmService.findAllLike(count);
    }

    @GetMapping(value = "/{id}")
    public Film find(@PathVariable final long id) {
        return filmStorage.findOrElseThrow(id);
    }

    @PostMapping
    public Film create(@Validated @RequestBody final Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@Validated @RequestBody final Film film) {
        return filmStorage.update(film);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable final long id, @PathVariable final long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void removeLike(@PathVariable final long id, @PathVariable final long userId) {
        filmService.removeLike(id, userId);
    }
}
