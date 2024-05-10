package ru.yandex.schedule.api.models;

public class BaseResponse {
    protected int statusCode;

    public BaseResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
