package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final ConditionsNotMetException e) {
        log.warn(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final MethodArgumentNotValidException e) {
        if (ObjectUtils.isEmpty(e.getBindingResult().getAllErrors().getFirst().getDefaultMessage())) {
            log.warn("Валидация не пройдена");
            return Map.of("error", "Валидация не пройдена");
        }

        log.warn(e.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
        return Map.of("error", e.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(final NotFoundException e) {
        log.warn(e.getMessage());
        return Map.of("error", e.getMessage());
    }
}
