package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.FriendsRowMapper;
import ru.yandex.practicum.filmorate.model.Friends;

import java.util.Optional;

@Slf4j
@Repository
public class FriendsRepository extends BaseRepository<Friends> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM friends WHERE id = ? AND friend_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM friends WHERE id IN (?, ?)";
    private static final String INSERT_QUERY = "INSERT INTO friends (id, friend_id) VALUES (?, ?)";

    public FriendsRepository(final JdbcTemplate jdbc, final FriendsRowMapper friendsRowMapper) {
        super(jdbc, friendsRowMapper);
    }

    public Optional<Friends> findById(final long id, final long idFriend) {
        return findOne(FIND_BY_ID_QUERY, id, idFriend);
    }

    public void remove(final long id, final long friendId) {
        delete(DELETE_QUERY, id, friendId);
        log.info("Пользователь с id = {}, удален из друзей id = {}", friendId, id);
    }

    public void save(final long id, final long friendId) {
        update(INSERT_QUERY, id, friendId);
        log.info("Пользователь с id = {}, отправил запрос для добавления в друзья к id = {}", friendId, id);
    }
}
