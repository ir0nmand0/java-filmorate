package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
public abstract class Id {
    private Long id;

    public void searchByFreeId(final Map<Long, ? extends Id> map) {
        long currentMaxId = map.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        if (currentMaxId == Long.MAX_VALUE || currentMaxId < 0L) {
            throw new ConditionsNotMetException("Недопустимый формат Id = " + currentMaxId);
        }

        this.id = ++currentMaxId;
    }
}
