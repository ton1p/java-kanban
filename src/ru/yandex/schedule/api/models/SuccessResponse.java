package ru.yandex.schedule.api.models;

public class SuccessResponse<T> extends BaseResponse {
    protected T data;

    public SuccessResponse(int statusCode, T data) {
        super(statusCode);
        this.data = data;
    }
}
