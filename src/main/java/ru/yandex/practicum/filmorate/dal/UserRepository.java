package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository extends BaseRepository<User> {
    private final FriendsRepository friendsRepository;
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_TWO_ID_QUERY = "SELECT * FROM users WHERE id = ? OR id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) "
            + "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? "
            + "WHERE id = ?";
    private static final String FIND_ALL_FRIENDS_QUERY = "SELECT * FROM users WHERE id IN ("
            + "SELECT friend_id FROM friends WHERE id = ? AND reciprocity = ?)";
    //Смотрим всех друзей пользователей, после группируем результат и фильтруем по количеству совпадений
    //и если их больше 1-го, тогда друг общий
    private static final String FIND_ALL_COMMON_FRIENDS_QUERY = "SELECT * FROM users WHERE id IN ( "
            + "SELECT friend_id FROM friends WHERE id IN (?, ?) AND reciprocity = ? "
            + "GROUP BY friend_id "
            + "HAVING COUNT(friend_id) > 1)";

    public UserRepository(final JdbcTemplate jdbc, final UserRowMapper userRowMapper,
                          final FriendsRepository friendsRepository) {
        super(jdbc, userRowMapper);
        this.friendsRepository = friendsRepository;
    }

    public Optional<User> findByEmail(final String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findById(final long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Collection<User> findById(final long id, final long friendId) {
        return findMany(FIND_BY_TWO_ID_QUERY, id, friendId);
    }

    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public User save(final User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getRealName(),
                user.getBirthday()
        );
        user.setId(id);
        log.info("Пользователь: {} добавлен в БД", user);
        return user;
    }

    public User update(final User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getRealName(),
                user.getBirthday(),
                user.getId()
        );
        log.info("Пользователь: {} обновлен в БД", user);
        return user;
    }

    public Collection<User> findAllFriends(final long id) {
        return findMany(FIND_ALL_FRIENDS_QUERY, id, true);
    }

    public Collection<User> findAllCommonFriends(final long id, final long friendId) {
        return findMany(FIND_ALL_COMMON_FRIENDS_QUERY, id, friendId, true);
    }

    public void saveFriends(final long id, final long friendId) {
        friendsRepository.save(id, friendId);
    }
}
