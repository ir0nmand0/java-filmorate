package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
public abstract class Id {
    @Positive
    private Long id;

    public void searchByFreeId(final Map<Long, ? extends Id> map) {
        long currentMaxId = map.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        this.id = ++currentMaxId;
    }
}
