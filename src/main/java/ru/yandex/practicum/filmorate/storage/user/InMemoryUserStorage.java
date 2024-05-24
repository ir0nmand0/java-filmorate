package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
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
        if (isDuplicateEmail(user)) {
            throw new ConditionsNotMetException("Email занят");
        }

        if (Objects.nonNull(user.getId())) {
            throw new ConditionsNotMetException("Id не допустим при создании пользователя");
        }

        user.setId(searchByFreeId());
        putUser(user);
        log.info("Пользователь: {} добавлен в БД", user);
        return user;
    }

    @Override
    public User update(final User user) {
        if (ObjectUtils.isEmpty(user.getId())) {
            throw new ConditionsNotMetException("Для обновления информации необходимо указать id");
        }

        findOrElseThrow(user.getId());
        putUser(user);
        log.info("Пользователь: {} обновлен в БД", user);
        return user;
    }

    @Override
    public User findOrElseThrow(final long id) {
        return getUser(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    @Override
    public Collection<User> findAllFriends(final long id) {
        ifEmptyThenPut(id);
        return friends.get(id).stream()
                .map(this::findOrElseThrow)
                .toList();
    }

    @Override
    public Collection<User> findAllCommonFriends(final long id, final long friendId) {
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
    public void addFriendOrElseThrow(final long id, final long friendId) {
        ifEmptyThenPut(id);
        ifEmptyThenPut(friendId);
        if (putFriend(id, friendId) && putFriend(friendId, id)) {
            log.info("Пользователь с id = {}, добавлен в друзья к id = {}", friendId, id);
            return;
        }

        throw new ConditionsNotMetException("Пользователи уже друзья");
    }

    @Override
    public void removeFriendOrElseThrow(final long id, final long friendId) {
        ifEmptyThenPut(id);
        ifEmptyThenPut(friendId);
        if (deleteFriend(id, friendId) && deleteFriend(friendId, id)) {
            log.info("Пользователь с id = {}, удален из друзей id = {}", friendId, id);
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

    private void ifEmptyThenPut(final long id) {
        findOrElseThrow(id);

        if (isEmptyInFriends(id)) {
            friends.put(id, new HashSet<>());
        }
    }

    @Override
    public Optional<User> getUser(final long id) {
        return Optional.ofNullable(users.get(id));
    }

    private boolean isEmptyInFriends(final long id) {
        return !friends.containsKey(id);
    }

    @Override
    public boolean isDuplicateEmail(final User user) {
        return users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(userEmail -> userEmail.replaceAll("\\s", "")
                        .compareToIgnoreCase(user.getEmail().replaceAll("\\s", "")) == 0);
    }

    private long searchByFreeId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        if (currentMaxId == Long.MAX_VALUE || currentMaxId < 0L) {
            throw new ConditionsNotMetException("Недопустимый формат Id пользователя = " + currentMaxId);
        }

        return ++currentMaxId;
    }
}
