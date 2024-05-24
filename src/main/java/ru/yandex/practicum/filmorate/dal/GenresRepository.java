package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.filmorate.dal.mapper.GenresRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;
import java.util.Set;

@Repository
public class GenresRepository extends BaseRepository<Genres> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO genres (id, genre_id) VALUES (?, ?)";

    public GenresRepository(final JdbcTemplate jdbc, final GenresRowMapper genresRowMapper) {
        super(jdbc, genresRowMapper);
    }

    public Collection<Genres> findById(final long id) {
        return findMany(FIND_BY_ID_QUERY, id);
    }

    public void save(final long id, final Set<Genre> genres) {
        if (ObjectUtils.isEmpty(genres)) {
            return;
        }

        genres.forEach(genre -> super.update(INSERT_QUERY, id, genre.getId()));
    }
}
