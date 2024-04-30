package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final HashMap<Long, Set<Long>> userLikes = new HashMap<>();
    private final TreeMap<Integer, Set<Long>> numberOfLikes = new TreeMap<>(Collections.reverseOrder());

    @Override
    public void addLike(final long id, final long userId) {
        filmStorage.findOrElseThrow(id);
        userStorage.findOrElseThrow(userId);
        ifEmptyThenPut(id);
        final int oldSize = getSize(id);
        ifEmptyThenPut(oldSize);

        if (!userLikes.get(id).add(userId)) {
            throw new ConditionsNotMetException(
                    String.format("Пользователь с id = %d уже поставил лайк фильму с id = %d", userId, id)
            );
        }

        numberOfLikes.get(oldSize).remove(id);
        final int size = userLikes.get(id).size();
        ifEmptyThenPut(size);
        numberOfLikes.get(size).add(id);
        log.info(String.format("Пользователь с id = %d, поставил лайк фильму с id = %d", userId, id));
    }

    @Override
    public void removeLike(final long id, final long userId) {
        filmStorage.findOrElseThrow(id);
        userStorage.findOrElseThrow(userId);
        final int oldSize = getSize(id);
        ifEmptyThenPut(oldSize);

        if (!userLikes.get(id).remove(userId)) {
            throw new ConditionsNotMetException(
                    String.format("Пользователь с id = %d не ставил лайк фильму с id = %d", userId, id)
            );
        }

        numberOfLikes.get(oldSize).remove(id);
        final int size = userLikes.get(id).size();
        ifEmptyThenPut(size);
        numberOfLikes.get(size).addAll(numberOfLikes.get(oldSize));
        numberOfLikes.get(oldSize).clear();
        log.info(String.format("Пользователь с id = %d, удалил лайк у фильма с id = %d", userId, id));
    }

    @Override
    public Collection<Film> findAllLike(final int count) {
        List<Film> films = new ArrayList<>();

        numberOfLikes.entrySet().stream()
                .limit(count)
                .forEach(integerSetEntry -> integerSetEntry.getValue()
                        .forEach(id -> films.add(filmStorage.findOrElseThrow(id))));

        return films;
    }

    private void ifEmptyThenPut(final long id) {
        if (!userLikes.containsKey(id)) {
            userLikes.put(id, new HashSet<>());
        }
    }

    private int getSize(final long id) {
        return userLikes.get(id).size();
    }

    private void ifEmptyThenPut(final int id) {
        if (!numberOfLikes.containsKey(id)) {
            numberOfLikes.put(id, new HashSet<>());
        }
    }
}
