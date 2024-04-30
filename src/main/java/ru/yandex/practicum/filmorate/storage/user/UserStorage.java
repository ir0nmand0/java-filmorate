package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();
    User create(final User user);
    User update(final User user);
    User findOrElseThrow(final long id);
}