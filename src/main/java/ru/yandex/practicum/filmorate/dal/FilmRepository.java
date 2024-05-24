package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> {
    private final GenresRepository genresRepository;
    private final GenreRepository genreRepository;
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM films WHERE name = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) "
            + "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, "
            + "duration = ? WHERE id = ?";
    private static final String FIND_ALL_POPULAR = "SELECT id, name, description, release_date, duration, mpa_id, "
            + "COUNT(l.user_id) AS count_likes FROM films AS f JOIN likes AS l ON f.id = l.film_id "
            + "GROUP BY f.id ORDER BY count_likes DESC LIMIT ?";

    public FilmRepository(final JdbcTemplate jdbc, final FilmRowMapper filmRowMapper,
                          final GenresRepository genresRepository, final GenreRepository genreRepository) {
        super(jdbc, filmRowMapper);
        this.genresRepository = genresRepository;
        this.genreRepository = genreRepository;
    }

    public Optional<Film> findByEmail(final String name) {
        return findOne(FIND_BY_NAME_QUERY, name);
    }

    public Optional<Film> findById(final long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Collection<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Collection<Film> findAllPopular(final int count) {
        return findMany(FIND_ALL_POPULAR, count);
    }

    public Film update(final Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );

        Set<Genre> genres = film.getGenres();

        if (ObjectUtils.isEmpty(genres)) {
            final Collection<Genres> genreList = genresRepository.findById(film.getId());

            if (!genreList.isEmpty()) {
                Set<Genre> genreSet = new LinkedHashSet<>();
                genreList.forEach(genre -> genreSet.add(genreRepository.findById(genre.idGenre())));
                film.setGenres(genreSet);
            }

        } else {
            genresRepository.save(film.getId(), film.getGenres());
        }

        log.info("Фильм: {} обновлен в БД", film);
        return film;
    }

    public Film save(final Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        genresRepository.save(film.getId(), film.getGenres());
        log.info("Фильм: {} добавлен в БД", film);
        return film;
    }
}
