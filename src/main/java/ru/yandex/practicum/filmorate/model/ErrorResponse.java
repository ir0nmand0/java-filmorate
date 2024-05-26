package ru.yandex.practicum.filmorate.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ErrorResponse(String error) {
    public ErrorResponse {
        log.warn(error);
    }
}
