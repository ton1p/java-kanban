package ru.yandex.schedule.managers.exceptions;

public class OverlapException extends RuntimeException {
    public OverlapException(String message) {
        super(message);
    }
}
