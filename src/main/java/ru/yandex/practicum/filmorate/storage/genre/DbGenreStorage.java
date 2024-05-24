package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class DbGenreStorage implements GenreStorage {
    private final GenreRepository genreRepository;

    @Override
    public Collection<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre findOrElseThrow(final int id) {
        return genreRepository.findById(id);
    }
}
