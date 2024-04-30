package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.PostmanNotFriendRemoveException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Override
    public void addFriend(final long id, final long friendId) {
        ifEmptyThenPut(id);
        ifEmptyThenPut(friendId);

        if (friends.get(id).add(friendId) && friends.get(friendId).add(id)) {
            log.info(String.format("Пользователь с id = %d, добавлен в друзья к id = %d", friendId, id));
            return;
        }

        throw new ConditionsNotMetException("Пользователи уже друзья");
    }

    @Override
    public void removeFriend(final long id, final long friendId) {
        ifEmptyThenPut(id);
        ifEmptyThenPut(friendId);

        if (friends.get(id).remove(friendId) && friends.get(friendId).remove(id)) {
            log.info(String.format("Пользователь с id = %d, удален из друзей id = %d", friendId, id));
            return;
        }

        throw new PostmanNotFriendRemoveException("Пользователи не друзья");
    }

    @Override
    public Collection<User> findAllFriends(final long id) {
        ifEmptyThenPut(id);
        return friends.get(id).stream()
                .map(userStorage::findOrElseThrow)
                .toList();
    }

    @Override
    public Collection<User> findAllFriends(final long id, final long friendId) {
        ifEmptyThenPut(id);
        ifEmptyThenPut(friendId);
        return friends.get(id).stream()
                .filter(commonId -> friends.get(friendId).contains(commonId))
                .map(userStorage::findOrElseThrow)
                .toList();
    }

    private void ifEmptyThenPut(final long id) {
        userStorage.findOrElseThrow(id);

        if (!friends.containsKey(id)) {
            friends.put(id, new HashSet<>());
        }
    }
}
