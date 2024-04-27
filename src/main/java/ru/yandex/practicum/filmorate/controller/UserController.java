package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Validated @RequestBody User user) {
        final boolean userNameIsEmpty = users.values()
                .stream()
                .map(User::getLogin)
                .anyMatch(userName -> userName.trim().equalsIgnoreCase(user.getLogin().trim()));

        if (userNameIsEmpty) {
            throw new ConditionsNotMetException("Логин занят");
        }

        user.searchByFreeId(users);
        users.put(user.getId(), user);
        log.info(String.format("Пользователь: %s добавлен в БД", user));
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    @PutMapping
    public User update(@Validated @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info(String.format("Пользователь: %s обновлен в БД", user));
            return user;
        }

        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }
}
