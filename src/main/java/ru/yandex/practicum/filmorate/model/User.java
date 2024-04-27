package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Jacksonized
@SuperBuilder
public class User extends Id {
    @Email
    private String email;
    @NotEmpty
    @NotBlank
    @Pattern(regexp = "^\\w{5,30}$")
    private String login;
    @Getter(AccessLevel.NONE)
    private String name;
    @PastOrPresent(message = "Дата рождения должна быть прошлым или сегодняшним числом")
    private LocalDate birthday;

    public String getName() {
        return ObjectUtils.isEmpty(name) ? login : name;
    }
}
