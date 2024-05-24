package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping(value = "/{id}/friends")
    public Collection<User> findAllFriends(@PathVariable final long id) {
        return userService.findAllFriends(id);
    }

    @GetMapping(value = "/{id}/friends/common/{friendId}")
    public Collection<User> findAllFriends(@PathVariable final long id, @PathVariable final long friendId) {
        return userService.findAllFriends(id, friendId);
    }

    @PostMapping
    public User create(@Validated @RequestBody final User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Validated @RequestBody final User user) {
        return userStorage.update(user);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable final long id, @PathVariable final long friendId) {
        isUnique(id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable final long id, @PathVariable final long friendId) {
        isUnique(id, friendId);
        userService.removeFriend(id, friendId);
    }

    private void isUnique(final long id, final long friendId) {
        if (id == friendId) {
            throw new ConditionsNotMetException("Пользователь не может отправить самому себе запрос");
        }
    }
}
