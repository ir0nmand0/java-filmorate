package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.jackson.FilmLocalDateDeserializer;
import ru.yandex.practicum.filmorate.jackson.GenreDeserializer;
import ru.yandex.practicum.filmorate.jackson.MpaDeserializer;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@Jacksonized
@EqualsAndHashCode(of = "id")
public class Film {
    private Long id;
    // https://www.kinopoisk.ru/media/article/4006428/
    @Size(min = 1, max = 100, message = "Минимальная длина имени фильма - 1 символ, а максимальная — 100")
    private String name;
    @Size(min = 1, max = 200, message = "Минимальная длина описания фильма - 1 символ, а максимальная — 200")
    private String description;
    @JsonDeserialize(using = FilmLocalDateDeserializer.class)
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше 0")
    private int duration;
    @JsonDeserialize(using = MpaDeserializer.class)
    private Mpa mpa;
    @JsonDeserialize(as = LinkedHashSet.class, contentUsing = GenreDeserializer.class)
    private Set<Genre> genres;
}
