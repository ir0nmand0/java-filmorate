package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;
import java.util.List;

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

    public Collection<Genre> findMany(final List<Genres> genres) {
        StringBuilder findByIdManyQuery = new StringBuilder("SELECT * FROM genre WHERE id IN (");
        int maxIndex = genres.size() - 1;

        for (int i = 0; i < maxIndex; ++i) {
            findByIdManyQuery.append(String.format("%d, ", genres.get(i).idGenre()));
        }

        findByIdManyQuery.append(String.format("%d)", genres.get(maxIndex).idGenre()));
        return jdbc.query(findByIdManyQuery.toString(), mapper);
    }

    public Genre findById(final int id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден"));
    }
}
