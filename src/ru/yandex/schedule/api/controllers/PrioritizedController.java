package ru.yandex.schedule.api.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.schedule.managers.interfaces.TaskManager;

import java.io.IOException;

public class PrioritizedController extends BaseController {
    public PrioritizedController(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") && pathParts.length == 2 && pathParts[1].equals("prioritized")) {
            sendResponse(exchange, this.taskManager.getPrioritizedTasks());
        } else {
            sendNotFoundEndpoint(exchange);
        }
    }
}
