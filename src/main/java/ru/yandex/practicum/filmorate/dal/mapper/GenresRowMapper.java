package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenresRowMapper implements RowMapper<Genres> {
    @Override
    public Genres mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Genres(
                rs.getLong("id"),
                rs.getInt("genre_id")
        );
    }
}
