package ru.yandex.practicum.filmorate.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.io.IOException;

public class MpaDeserializer extends StdDeserializer<Mpa> {
    private MpaRepository mpaRepository;

    protected MpaDeserializer() {
        this(Mpa.class);
    }

    protected MpaDeserializer(Class<?> vc) {
        super(vc);
    }

    @Autowired
    public void setMpaRepository(final MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    @Override
    public Mpa deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final JsonNode tree = jsonParser.getCodec().readTree(jsonParser);

        try {
            return mpaRepository.findById(tree.get("id").asInt());
        } catch (NotFoundException e) {
            throw new ConditionsNotMetException(e.getMessage());
        }
    }
}
