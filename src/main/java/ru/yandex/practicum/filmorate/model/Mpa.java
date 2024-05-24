package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Mpa {
    @Positive
    private int id;
    private String name;
}