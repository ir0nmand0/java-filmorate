package ru.yandex.practicum.filmorate.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;

import java.io.IOException;
import java.time.Duration;

public class CustomDurationDeserializer extends StdDeserializer<Duration> {
    public CustomDurationDeserializer() {
        this(null);
    }

    public CustomDurationDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        Long durationMinutes = jsonParser.getLongValue();

        if (durationMinutes <= 0) {
            throw new ConditionsNotMetException("Продолжительность фильма должна быть больше 0");
        }

        return Duration.ofMinutes(durationMinutes);
    }
}
