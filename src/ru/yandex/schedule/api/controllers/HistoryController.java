package ru.yandex.schedule.api.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.schedule.managers.interfaces.TaskManager;

import java.io.IOException;

public class HistoryController extends BaseController {

    public HistoryController(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        if (method.equals("GET")) {
            if (pathParts.length == 2 && pathParts[1].equals("history")) {
                sendResponse(exchange, this.taskManager.getHistory());
            } else {
                sendNotFoundEndpoint(exchange);
            }
        } else {
            sendNotFoundEndpoint(exchange);
        }
    }
}
