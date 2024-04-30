package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

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

        user.searchByFreeId(users);
        users.put(user.getId(), user);
        log.info(String.format("Пользователь: %s добавлен в БД", user));
        return user;
    }

    @Override
    public User update(User user) {
        findOrElseThrow(user.getId());
        users.put(user.getId(), user);
        log.info(String.format("Пользователь: %s обновлен в БД", user));
        return user;
    }

    @Override
    public User findOrElseThrow(final long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return users.get(id);
    }

    private boolean isDuplicated(final User user) {
        return users.values()
                .stream()
                .map(User::getLogin)
                .anyMatch(userLogin -> userLogin.replaceAll("\\s", "")
                        .compareToIgnoreCase(user.getLogin().replaceAll("\\s", "")) == 0);
    }
}
