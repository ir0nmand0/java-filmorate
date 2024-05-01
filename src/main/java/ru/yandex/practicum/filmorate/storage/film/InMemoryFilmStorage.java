package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final HashMap<Long, Set<Long>> userLikes = new HashMap<>();
    private final TreeMap<Integer, Set<Long>> numberOfLikes = new TreeMap<>(Collections.reverseOrder());

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

        film.setId(searchByFreeId());
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
        if (isEmptyInFilms(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        return films.get(id);
    }

    @Override
    public Collection<Film> findAllLike(final int count) {
        List<Film> popular = new ArrayList<>();

        numberOfLikes.entrySet().stream()
                .limit(count)
                .forEach(integerSetEntry -> integerSetEntry.getValue()
                        .forEach(id -> popular.add(findOrElseThrow(id))));

        return popular;
    }

    @Override
    public void ifEmptyThenPutInUserLikes(final long id) {
        if (isEmptyInUserLikes(id)) {
            userLikes.put(id, new HashSet<>());
        }
    }

    @Override
    public int getSizeUserLikes(final long id) {
        return userLikes.get(id).size();
    }

    @Override
    public void addLikeInUserLikesOrElseThrow(final long id, final long userId) {
        if (!userLikes.get(id).add(userId)) {
            throw new ConditionsNotMetException(
                    String.format("Пользователь с id = %d уже поставил лайк фильму с id = %d", userId, id)
            );
        }
    }

    @Override
    public void addLikeInNumberOfLikesOrElseThrow(final int size, final long id, final long userId) {
        numberOfLikes.get(size).add(id);
        log.info(String.format("Пользователь с id = %d, поставил лайк фильму с id = %d", userId, id));
    }

    @Override
    public void transferLikesInInNumberOfLikes(final int size, final int oldSize) {
        numberOfLikes.get(size).addAll(numberOfLikes.get(oldSize));
        numberOfLikes.get(oldSize).clear();
    }

    @Override
    public void removeLikeFromNumberOfLikes(final int size, final long id) {
        numberOfLikes.get(size).remove(id);
    }

    @Override
    public void removeLikeFromUserLikesOrElseThrow(final long id, final long userId) {
        if (!userLikes.get(id).remove(userId)) {
            throw new ConditionsNotMetException(
                    String.format("Пользователь с id = %d не ставил лайк фильму с id = %d", userId, id)
            );
        }

        log.info(String.format("Пользователь с id = %d, удалил лайк у фильма с id = %d", userId, id));
    }

    @Override
    public void ifEmptyThenPutInNumberOfLikes(final int id) {
        if (isEmptyInNumberOfLikes(id)) {
            numberOfLikes.put(id, new HashSet<>());
        }
    }

    private boolean isEmptyInNumberOfLikes(final int id) {
        return !numberOfLikes.containsKey(id);
    }

    private boolean isEmptyInUserLikes(final long id) {
        return !userLikes.containsKey(id);
    }

    private boolean isEmptyInFilms(final long id) {
        return !films.containsKey(id);
    }

    private boolean isDuplicated(final Film film) {
        return films.values()
                .stream()
                .map(Film::getName)
                .anyMatch(filmName -> filmName.replaceAll("\\s", "")
                        .compareToIgnoreCase(film.getName().replaceAll("\\s", "")) == 0);
    }

    private long searchByFreeId() {
        long currentMaxId = films.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        if (currentMaxId == Long.MAX_VALUE || currentMaxId < 0L) {
            throw new ConditionsNotMetException("Недопустимый формат Id фильма = " + currentMaxId);
        }

        return  ++currentMaxId;
    }
}
