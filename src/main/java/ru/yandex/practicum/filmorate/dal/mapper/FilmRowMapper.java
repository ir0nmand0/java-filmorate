package ru.yandex.practicum.filmorate.dal.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.GenresRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;
    private final GenresRepository genresRepository;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        final long id = rs.getLong("id");
        final Set<Genre> genres = new LinkedHashSet<>();
        final Collection<Genres> genreList = genresRepository.findById(id);

        if (!genreList.isEmpty()) {
            try {
                genreList.forEach(genre -> genres.add(genreRepository.findById(genre.idGenre())));
            } catch (NotFoundException e) {
                throw new ConditionsNotMetException(e.getMessage());
            }
        }

        Mpa mpa = null;

        try {
            mpa = mpaRepository.findById(rs.getInt("mpa_id"));
        } catch (NotFoundException e) {
            throw new ConditionsNotMetException(e.getMessage());
        }

        return Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .genres(genres)
                .build();
    }
}
