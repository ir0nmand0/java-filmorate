package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    void addFriend(final long id, final long friendId);
    void removeFriend(final long id, final long friendId);
    Collection<User> findAllFriends(final long id);
    Collection<User> findAllFriends(final long id, final long friendId);
}
