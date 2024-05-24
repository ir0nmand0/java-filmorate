package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final HashMap<Long, Set<Long>> favoriteFilms = new HashMap<>();
    private final TreeMap<Integer, Set<Long>> numberLikes = new TreeMap<>(Collections.reverseOrder());

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(final Film film) {
        if (isDuplicatedName(film)) {
            throw new ConditionsNotMetException("Фильм уже добавлен");
        }

        if (Objects.nonNull(film.getId())) {
            throw new ConditionsNotMetException("Id не допустим при создании фильма");
        }

        film.setId(searchByFreeId());
        films.put(film.getId(), film);
        log.info("Фильм: {} добавлен в БД", film);
        return film;
    }

    @Override
    public Film update(final Film film) {
        if (ObjectUtils.isEmpty(film.getId())) {
            throw new ConditionsNotMetException("Для обновления информации необходимо указать id");
        }

        findOrElseThrow(film.getId());
        films.put(film.getId(), film);
        log.info("Фильм: {} обновлен в БД", film);
        return film;
    }

    @Override
    public Film findOrElseThrow(final long id) {
        return getFilm(id).orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    @Override
    public Optional<Film> getFilm(final long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> findAllLike(final int count) {
        List<Film> popular = new ArrayList<>();

        numberLikes.entrySet().stream()
                .limit(count)
                .forEach(integerSetEntry -> integerSetEntry.getValue()
                        .forEach(id -> popular.add(findOrElseThrow(id))));

        return popular;
    }

    private void ifEmptyThenPutInFavoriteFilms(final long id) {
        if (isEmptyInUserLikes(id)) {
            favoriteFilms.put(id, new HashSet<>());
        }
    }

    private int getSizeFavoriteFilms(final long id) {
        return favoriteFilms.get(id).size();
    }

    @Override
    public void addLikeInFavoriteFilmsOrElseThrow(final long id, final long userId) {
        ifEmptyThenPutInFavoriteFilms(id);
        ifEmptyThenPutInNumberLikes(getSizeFavoriteFilms(id));
        if (!favoriteFilms.get(id).add(userId)) {
            throw new ConditionsNotMetException(
                    String.format("Пользователь с id = %d уже поставил лайк фильму с id = %d", userId, id)
            );
        }
        addLikeInNumberLikesOrElseThrow(id, userId);
    }

    private void addLikeInNumberLikesOrElseThrow(final long id, final long userId) {
        int idLike = getSizeFavoriteFilms(id);
        ifEmptyThenPutInNumberLikes(++idLike);
        numberLikes.get(idLike).add(id);
        log.info("Пользователь с id = {}, поставил лайк фильму с id = {}", userId, id);
    }

    private void transferLikesInNumberLikes(final int oldId, final int newId) {
        numberLikes.get(newId).addAll(numberLikes.get(oldId));
        numberLikes.get(oldId).clear();
        numberLikes.remove(oldId);
    }

    private void removeLikeFromNumberLikes(final long id) {
        numberLikes.get(getSizeFavoriteFilms(id)).remove(id);
    }

    @Override
    public void removeLikeFromFavoriteFilmsOrElseThrow(final long id, final long userId) {
        int idLike = getSizeFavoriteFilms(id);
        ifEmptyThenPutInNumberLikes(idLike);

        if (!favoriteFilms.get(id).remove(userId)) {
            throw new ConditionsNotMetException(
                    String.format("Пользователь с id = %d не ставил лайк фильму с id = %d", userId, id)
            );
        }

        log.info("Пользователь с id = {}, удалил лайк у фильма с id = {}", userId, id);
        removeLikeFromNumberLikes(id);
        transferLikesInNumberLikes(idLike, --idLike);
    }

    private void ifEmptyThenPutInNumberLikes(final int id) {
        if (isEmptyInNumberOfLikes(id)) {
            numberLikes.put(id, new HashSet<>());
        }
    }

    private boolean isEmptyInNumberOfLikes(final int id) {
        return !numberLikes.containsKey(id);
    }

    private boolean isEmptyInUserLikes(final long id) {
        return !favoriteFilms.containsKey(id);
    }

    private boolean isEmptyInFilms(final long id) {
        return !films.containsKey(id);
    }

    @Override
    public boolean isDuplicatedName(final Film film) {
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

        return ++currentMaxId;
    }
}
