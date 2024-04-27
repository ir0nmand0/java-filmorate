package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.jackson.CustomDurationDeserializer;
import ru.yandex.practicum.filmorate.jackson.CustomLocalDateDeserializer;

import java.time.Duration;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Jacksonized
@SuperBuilder
public class Film extends Id {
    // https://www.kinopoisk.ru/media/article/4006428/
    @Size(min = 1, max = 100, message = "Минимальная длина имени фильма - 1 символ, а максимальная — 100")
    private String name;
    @Size(min = 1, max = 200, message = "Минимальная длина описания фильма - 1 символ, а максимальная — 200")
    private String description;
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate releaseDate;
    @Getter(AccessLevel.NONE)
    @JsonDeserialize(using = CustomDurationDeserializer.class)
    private Duration duration;

    public Long getDuration() {
        return duration.toMinutes();
    }
}
