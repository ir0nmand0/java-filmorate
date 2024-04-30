package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User create(final User user);

    User update(final User user);

    User findOrElseThrow(final long id);

    void addFriend(long id, long friendId);

    void removeFriend(long id, long friendId);

    Collection<User> findAllFriends(long id);

    Collection<User> findAllFriends(long id, long friendId);
}
