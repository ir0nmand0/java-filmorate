package ru.yandex.practicum.filmorate.exception;

public class ConditionsNotMetException extends IllegalArgumentException {
    public ConditionsNotMetException(String message) {
        super(message);
    }
}