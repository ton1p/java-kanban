package ru.yandex.schedule.api.models;

public class ErrorResponse extends BaseResponse {
    protected String message;

    public ErrorResponse(int statusCode, String message) {
        super(statusCode);
        this.message = message;
    }
}
