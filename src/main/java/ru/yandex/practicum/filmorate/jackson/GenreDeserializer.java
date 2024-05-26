package ru.yandex.practicum.filmorate.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.io.IOException;

public class GenreDeserializer extends StdDeserializer<Genre> {
    private GenreRepository genreRepository;

    protected GenreDeserializer() {
        this(Genre.class);
    }

    protected GenreDeserializer(Class<?> vc) {
        super(vc);
    }

    @Autowired
    public void setMpaRepository(final GenreRepository mpaRepository) {
        this.genreRepository = mpaRepository;
    }

    @Override
    public Genre deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final JsonNode tree = jsonParser.getCodec().readTree(jsonParser);

        try {
            return genreRepository.findById(tree.get("id").asInt());
        } catch (NotFoundException e) {
            throw new ConditionsNotMetException(e.getMessage());
        }
    }
}
