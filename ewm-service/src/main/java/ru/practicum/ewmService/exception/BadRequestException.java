package ru.practicum.ewmService.exception;

public class BadRequestException extends IllegalArgumentException {
    public BadRequestException(final String message) {
        super(message);
    }
}
