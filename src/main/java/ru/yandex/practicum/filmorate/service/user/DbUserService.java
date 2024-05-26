package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    @Override
    public void addFriend(final long id, final long friendId) {
        userStorage.addFriendOrElseThrow(id, friendId);
    }

    @Override
    public void removeFriend(final long id, final long friendId) {
        userStorage.removeFriendOrElseThrow(id, friendId);
    }

    @Override
    public Collection<User> findAllFriends(final long id) {
        return userStorage.findAllFriends(id);
    }

    @Override
    public Collection<User> findAllFriends(final long id, final long friendId) {
        return userStorage.findAllCommonFriends(id, friendId);
    }
}
