package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    private final FilmRepository filmRepository;
    private final LikesRepository likesRepository;
    private final DbUserStorage dbUserStorage;

    @Override
    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    @Override
    public Film create(final Film film) {
        if (Objects.nonNull(film.getId())) {
            throw new ConditionsNotMetException("Id не допустим при создании фильма");
        }

        return filmRepository.save(film);
    }

    @Override
    public Film update(final Film film) {
        if (ObjectUtils.isEmpty(film.getId())) {
            throw new ConditionsNotMetException("Для обновления информации необходимо указать id");
        }

        findOrElseThrow(film.getId());
        filmRepository.update(film);
        return film;
    }

    @Override
    public Film findOrElseThrow(final long id) {
        return getFilm(id).orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    @Override
    public Optional<Film> getFilm(final long id) {
        return filmRepository.findById(id);
    }

    @Override
    public Collection<Film> findAllLike(final int count) {
        return filmRepository.findAllPopular(count);
    }

    @Override
    public void addLikeInFavoriteFilmsOrElseThrow(final long id, final long userId) {
        findOrElseThrow(id);
        dbUserStorage.findOrElseThrow(userId);
        if (likesRepository.isExists(id, userId)) {
            throw new ConditionsNotMetException(
                    String.format("Пользователь с id = %d уже поставил лайк фильму с id = %d", id, userId)
            );
        }

        likesRepository.save(id, userId);
    }

    @Override
    public void removeLikeFromFavoriteFilmsOrElseThrow(final long id, final long userId) {
        findOrElseThrow(id);
        dbUserStorage.findOrElseThrow(userId);

        if (likesRepository.isExists(id, userId)) {
            likesRepository.remove(id, userId);
            return;
        }

        throw new ConditionsNotMetException(
                String.format("Пользователь с id = %d не ставил лайк фильму с id = %d", id, userId)
        );
    }

    @Override
    public boolean isDuplicatedName(final Film film) {
        return filmRepository.findByEmail(film.getName()).isPresent();
    }
}
