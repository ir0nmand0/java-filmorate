package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.jackson.FilmDurationDeserializer;
import ru.yandex.practicum.filmorate.jackson.FilmLocalDateDeserializer;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
@Jacksonized
public class Film {
    private Long id;
    // https://www.kinopoisk.ru/media/article/4006428/
    @Size(min = 1, max = 100, message = "Минимальная длина имени фильма - 1 символ, а максимальная — 100")
    private String name;
    @Size(min = 1, max = 200, message = "Минимальная длина описания фильма - 1 символ, а максимальная — 200")
    private String description;
    @JsonDeserialize(using = FilmLocalDateDeserializer.class)
    private LocalDate releaseDate;
    @Getter(AccessLevel.NONE)
    @JsonDeserialize(using = FilmDurationDeserializer.class)
    private Duration duration;

    public Long getDuration() {
        return duration.toMinutes();
    }
}
