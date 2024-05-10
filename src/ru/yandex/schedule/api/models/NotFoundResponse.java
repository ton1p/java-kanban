package ru.yandex.schedule.api.models;

public class NotFoundResponse extends ErrorResponse {
    public NotFoundResponse(String message) {
        super(404, message);
    }
}
