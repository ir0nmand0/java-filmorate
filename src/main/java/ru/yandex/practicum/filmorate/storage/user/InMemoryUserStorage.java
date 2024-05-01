package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.PostmanNotFriendRemoveException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(final User user) {
        if (isDuplicated(user)) {
            throw new ConditionsNotMetException("Логин занят");
        }

        if (Objects.nonNull(user.getId())) {
            throw new ConditionsNotMetException("Id не допустим при создании пользователя");
        }

        user.setId(searchByFreeId());
        putUser(user);
        log.info(String.format("Пользователь: %s добавлен в БД", user));
        return user;
    }

    @Override
    public User update(User user) {
        findOrElseThrow(user.getId());
        putUser(user);
        log.info(String.format("Пользователь: %s обновлен в БД", user));
        return user;
    }

    @Override
    public User findOrElseThrow(final long id) {
        if (isEmptyInUsers(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return users.get(id);
    }

    @Override
    public Collection<User> findAllFriends(final long id) {
        ifEmptyThenPut(id);
        return friends.get(id).stream()
                .map(this::findOrElseThrow)
                .toList();
    }

    @Override
    public Collection<User> findAllFriends(final long id, final long friendId) {
        ifEmptyThenPut(id);
        ifEmptyThenPut(friendId);
        return friends.get(id).stream()
                .filter(commonId -> friends.get(friendId).contains(commonId))
                .map(this::findOrElseThrow)
                .toList();
    }

    private void putUser(final User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void putFriendOrElseThrow(final long id, final long friendId) {
        if (putFriend(id, friendId) && putFriend(friendId, id)) {
            log.info(String.format("Пользователь с id = %d, добавлен в друзья к id = %d", friendId, id));
            return;
        }

        throw new ConditionsNotMetException("Пользователи уже друзья");
    }

    @Override
    public void removeFriendOrElseThrow(final long id, final long friendId) {
        if (deleteFriend(id, friendId) && deleteFriend(friendId, id)) {
            log.info(String.format("Пользователь с id = %d, удален из друзей id = %d", friendId, id));
            return;
        }

        throw new PostmanNotFriendRemoveException("Пользователи не друзья");
    }

    private boolean putFriend(final long id, final long friendId) {
        return friends.get(id).add(friendId);
    }

    private boolean deleteFriend(final long id, final long friendId) {
        return friends.get(id).remove(friendId);
    }

    @Override
    public void ifEmptyThenPut(final long id) {
        findOrElseThrow(id);

        if (isEmptyInFriends(id)) {
            friends.put(id, new HashSet<>());
        }
    }

    private boolean isEmptyInUsers(final long id) {
        return !users.containsKey(id);
    }

    private boolean isEmptyInFriends(final long id) {
        return !friends.containsKey(id);
    }

    @Override
    public boolean isDuplicated(final User user) {
        return users.values()
                .stream()
                .map(User::getLogin)
                .anyMatch(userLogin -> userLogin.replaceAll("\\s", "")
                        .compareToIgnoreCase(user.getLogin().replaceAll("\\s", "")) == 0);
    }

    private long searchByFreeId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        if (currentMaxId == Long.MAX_VALUE || currentMaxId < 0L) {
            throw new ConditionsNotMetException("Недопустимый формат Id пользователя = " + currentMaxId);
        }

        return  ++currentMaxId;
    }
}
