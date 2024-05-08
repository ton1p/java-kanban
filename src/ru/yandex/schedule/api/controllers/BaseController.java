package ru.yandex.schedule.api.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.schedule.api.models.ErrorResponse;
import ru.yandex.schedule.api.models.NotFoundResponse;
import ru.yandex.schedule.api.models.SuccessResponse;
import ru.yandex.schedule.managers.interfaces.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseController implements HttpHandler {
    protected final TaskManager taskManager;

    protected final Gson gson;

    protected BaseController(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendCreatedStatus(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    protected void sendResponse(HttpExchange exchange, Object data) throws IOException {
        SuccessResponse<Object> successResponse = new SuccessResponse<>(200, data);
        String json = gson.toJson(successResponse);
        byte[] bytesOfResponse = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, bytesOfResponse.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytesOfResponse);
        }

        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        NotFoundResponse notFoundResponse = new NotFoundResponse(message);
        sendError(exchange, notFoundResponse);
    }

    protected void sendHasOverlap(HttpExchange exchange) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(406, "Задача пересекается по времени");
        sendError(exchange, errorResponse);
    }

    protected void sendError(HttpExchange exchange, ErrorResponse errorResponse) throws IOException {
        String json = gson.toJson(errorResponse);
        byte[] bytesOfResponse = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(errorResponse.getStatusCode(), bytesOfResponse.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytesOfResponse);
        }

        exchange.close();
    }

    protected void sendNotFoundEndpoint(HttpExchange exchange) throws IOException {
        byte[] byteOfResponse = "Такого эндпоинта нет".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(400, byteOfResponse.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(byteOfResponse);
        }

        exchange.close();
    }

    protected void sendSuccessStatus(HttpExchange exchange, String message) throws IOException {
        byte[] byteOfResponse = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, byteOfResponse.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(byteOfResponse);
        }

        exchange.close();
    }
}
