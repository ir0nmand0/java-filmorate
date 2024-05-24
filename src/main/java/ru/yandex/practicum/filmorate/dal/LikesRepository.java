package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.LikesRowMapper;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Likes;

@Slf4j
@Repository
public class LikesRepository extends BaseRepository<Likes> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM likes WHERE film_id = ?";
    private static final String FIND_BY_ID_USER_QUERY = "SELECT * FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM likes WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

    public LikesRepository(final JdbcTemplate jdbc, final LikesRowMapper likesRowMapper) {
        super(jdbc, likesRowMapper);
    }

    public Likes findById(final long id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new ConditionsNotMetException("Фильм с id = " + id + " не найден"));
    }

    public boolean isExists(final long id, final long userId) {
        return findOne(FIND_BY_ID_USER_QUERY, id, userId).isPresent();
    }

    public void remove(final long id, final long userId) {
        delete(DELETE_QUERY, id);
        log.info("Лайк у фильма с id = {}, был удален пользователем с id = {}", id, userId);
    }

    public void save(final long id, final long userId) {
        update(INSERT_QUERY, id, userId);
        log.info("Пользователь с id = {}, поставил лайк фильму с id = {}", userId, id);
    }
}
