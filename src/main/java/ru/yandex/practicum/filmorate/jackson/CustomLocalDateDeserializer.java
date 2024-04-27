package ru.yandex.practicum.filmorate.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CustomLocalDateDeserializer extends StdDeserializer<LocalDate> {
    public CustomLocalDateDeserializer() {
        this(null);
    }

    public CustomLocalDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String date = jsonParser.getText();
        try {
            LocalDate releaseDate = LocalDate.parse(date);

            if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ConditionsNotMetException("Дата релиза фильма должна быть после 28.12.1895");
            }

            return releaseDate;
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
    }
}
