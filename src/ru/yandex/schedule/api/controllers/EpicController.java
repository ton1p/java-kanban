package ru.yandex.schedule.api.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.schedule.api.models.ErrorResponse;
import ru.yandex.schedule.managers.exceptions.NotFoundException;
import ru.yandex.schedule.managers.interfaces.TaskManager;
import ru.yandex.schedule.tasks.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicController extends BaseController {
    public EpicController(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        String[] pathParts = path.split("/");

        switch (method) {
            case "GET": {
                getHandle(exchange, pathParts);
                break;
            }
            case "POST": {
                postHandle(exchange, pathParts);
                break;
            }
            case "DELETE": {
                deleteHandler(exchange, pathParts);
                break;
            }
            default: {
                sendNotFoundEndpoint(exchange);
            }
        }
    }

    private void getHandle(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2 && strIsEpics(pathParts[1])) {
            List<Epic> epicsList = this.taskManager.getEpicsList();
            sendResponse(exchange, epicsList);
        } else if (pathParts.length == 3 && strIsEpics(pathParts[1]) && strIsId(pathParts[2])) {
            int id = Integer.parseInt(pathParts[2]);
            try {
                sendResponse(exchange, this.taskManager.getEpicById(id));
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        } else if (
                pathParts.length == 4 &&
                        strIsEpics(pathParts[1]) &&
                        strIsId(pathParts[2]) &&
                        pathParts[3].equals("subtasks")
        ) {
            int epicId = Integer.parseInt(pathParts[2]);
            try {
                sendResponse(exchange, this.taskManager.getEpicSubTasks(epicId));
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        } else {
            sendNotFoundEndpoint(exchange);
        }
    }

    private void postHandle(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2 && strIsEpics(pathParts[1])) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(body);

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                JsonElement id = jsonObject.get("id");
                JsonElement name = jsonObject.get("name");
                JsonElement description = jsonObject.get("description");

                if (name == null || description == null) {
                    sendError(exchange, new ErrorResponse(400, "Не заполнены обязательные поля"));
                } else {
                    String nameValue = name.getAsString();
                    String descriptionValue = description.getAsString();

                    Epic epic = new Epic(nameValue, descriptionValue);

                    try {
                        if (id != null && id.getAsInt() != 0) {
                            epic.setId(id.getAsInt());
                            this.taskManager.updateEpic(epic);
                        } else {
                            this.taskManager.addEpic(epic);
                        }
                        sendCreatedStatus(exchange);
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                }
            } else {
                sendError(exchange, new ErrorResponse(400, "Тело запроса не в формате json"));
            }
        } else {
            sendNotFoundEndpoint(exchange);
        }
    }

    private void deleteHandler(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3 && strIsEpics(pathParts[1]) && strIsId(pathParts[2])) {
            int id = Integer.parseInt(pathParts[2]);
            try {
                this.taskManager.removeEpic(id);
                sendSuccessStatus(exchange, "Эпик удален");
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        } else {
            sendNotFoundEndpoint(exchange);
        }
    }

    private boolean strIsId(String target) {
        return target.matches("\\d+");
    }

    private boolean strIsEpics(String target) {
        return target.equals("epics");
    }
}
