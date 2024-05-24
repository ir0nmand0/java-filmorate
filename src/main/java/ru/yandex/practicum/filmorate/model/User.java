package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.ObjectUtils;
import java.time.LocalDate;

@Data
@Builder
@Jacksonized
@EqualsAndHashCode(of = {"email"})
public class User {
    private Long id;
    @Email
    private String email;
    @NotNull
    @Pattern(regexp = "^\\w{5,30}$", message = "Логин должен быть от 5 до 30 символов и без пробелов")
    private String login;
    @Getter(AccessLevel.NONE)
    private String name;
    @PastOrPresent(message = "Дата рождения должна быть прошлым или сегодняшним числом")
    private LocalDate birthday;

    public String getName() {
        return ObjectUtils.isEmpty(name) ? login : name;
    }

    public String getRealName() {
        return name;
    }
}
