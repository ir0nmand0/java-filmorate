package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.filmorate.dal.FriendsRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.PostmanNotFriendRemoveException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbUserStorage implements UserStorage {
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User create(final User user) {
        if (isDuplicateEmail(user)) {
            throw new ConditionsNotMetException("Email занят");
        }

        if (Objects.nonNull(user.getId())) {
            throw new ConditionsNotMetException("Id не допустим при создании пользователя");
        }

        return userRepository.save(user);
    }

    @Override
    public User update(final User user) {
        if (ObjectUtils.isEmpty(user.getId())) {
            throw new ConditionsNotMetException("Для обновления информации необходимо указать id");
        }

        findOrElseThrow(user.getId());
        return userRepository.update(user);
    }

    @Override
    public User findOrElseThrow(final long id) {
        return getUser(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    @Override
    public void findOrElseThrow(final long id, final long friendId) {
        final Collection<User> users = getUser(id, friendId);

        if (users.size() < 2) {
            for (final User user : users) {
                if (user.getId() != id || user.getId() != friendId) {
                    throw new NotFoundException(
                            "Пользователь с id = " + (user.getId().equals(id) ? friendId : id) + " не найден"
                    );
                }
            }
        }
    }

    @Override
    public Collection<User> findAllFriends(final long id) {
        findOrElseThrow(id);
        return userRepository.findAllFriends(id);
    }

    @Override
    public Collection<User> findAllCommonFriends(final long id, final long friendId) {
        findOrElseThrow(id, friendId);
        return userRepository.findAllCommonFriends(id, friendId);
    }

    @Override
    public void addFriendOrElseThrow(final long id, final long friendId) {
        findOrElseThrow(id, friendId);
        Optional<Friends> friends = friendsRepository.findById(id, friendId);

        if (friends.isEmpty()) {
            userRepository.saveFriends(id, friendId);
            return;
        }

        if (!friends.get().reciprocity()) {
            throw new ConditionsNotMetException(
                    String.format("Пользователь c id = %d уже отправил запрос для добавления в друзья к id = %d",
                            friendId, id)
            );
        }

        throw new ConditionsNotMetException("Пользователи уже друзья");
    }

    @Override
    public void removeFriendOrElseThrow(final long id, final long friendId) {
        findOrElseThrow(id, friendId);

        if (friendsRepository.findById(id, friendId).isEmpty()) {
            throw new PostmanNotFriendRemoveException("Пользователи не друзья");
        }

        friendsRepository.remove(id, friendId);
    }

    @Override
    public boolean isDuplicateEmail(final User user) {
        return userRepository.findByEmail(user.getEmail()).isPresent();
    }

    @Override
    public Optional<User> getUser(final long id) {
        return userRepository.findById(id);
    }

    @Override
    public Collection<User> getUser(final long id, final long friendId) {
        return userRepository.findById(id, friendId);
    }
}
