package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    User create(final User user);

    User update(final User user);

    User findOrElseThrow(final long id);

    Collection<User> findAllFriends(final long id);

    Collection<User> findAllCommonFriends(final long id, final long friendId);

    void addFriendOrElseThrow(final long id, final long friendId);

    void removeFriendOrElseThrow(final long id, final long friendId);

    Optional<User> getUser(final long id);

    boolean isDuplicateEmail(final User user);
}
