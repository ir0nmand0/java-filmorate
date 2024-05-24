package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";

    public GenreRepository(final JdbcTemplate jdbc, final GenreRowMapper genreRowMapper) {
        super(jdbc, genreRowMapper);
    }

    public Collection<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre findById(final int id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден"));
    }
}
