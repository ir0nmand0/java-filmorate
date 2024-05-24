package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Likes;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikesRowMapper implements RowMapper<Likes> {
    @Override
    public Likes mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Likes(rs.getLong("film_id"), rs.getLong("user_id"));
    }
}
