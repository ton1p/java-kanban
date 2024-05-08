package ru.yandex.schedule.api.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.schedule.api.models.ErrorResponse;
import ru.yandex.schedule.managers.exceptions.NotFoundException;
import ru.yandex.schedule.managers.exceptions.OverlapException;
import ru.yandex.schedule.managers.interfaces.TaskManager;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class TaskController extends BaseController {
    public TaskController(TaskManager taskManager, Gson gson) {
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
        if (pathParts.length == 2 && strIsTasks(pathParts[1])) {
            List<Task> taskList = this.taskManager.getTasksList();
            sendResponse(exchange, taskList);
        } else if (pathParts.length == 3 && strIsTasks(pathParts[1]) && strIsId(pathParts[2])) {
            int id = Integer.parseInt(pathParts[2]);
            try {
                sendResponse(exchange, this.taskManager.getTaskById(id));
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        } else {
            sendNotFoundEndpoint(exchange);
        }
    }

    private void postHandle(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2 && strIsTasks(pathParts[1])) {
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(body);

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonElement id = jsonObject.get("id");
                    JsonElement name = jsonObject.get("name");
                    JsonElement description = jsonObject.get("description");
                    JsonElement status = jsonObject.get("status");
                    JsonElement duration = jsonObject.get("duration");
                    JsonElement startTime = jsonObject.get("startTime");

                    if (name == null || description == null || status == null) {
                        sendError(exchange, new ErrorResponse(400, "Не заполнены обязательные поля"));
                    } else {
                        String nameValue = name.getAsString();
                        String descriptionValue = description.getAsString();
                        Status statusValue = Status.getStatusByString(status.getAsString());

                        Task task = new Task(nameValue, descriptionValue, statusValue);

                        if (duration != null && startTime != null) {
                            Duration durationValue = Duration.ofMinutes(duration.getAsLong());
                            Instant startTimeValue = Instant.parse(startTime.getAsString());
                            task.setDuration(durationValue);
                            task.setStartTime(startTimeValue);
                        }

                        try {
                            if (id != null && id.getAsInt() != 0) {
                                task.setId(id.getAsInt());
                                try {
                                    this.taskManager.updateTask(task);
                                } catch (NotFoundException e) {
                                    sendNotFound(exchange, e.getMessage());
                                }
                            } else {
                                this.taskManager.addTask(task);
                            }
                            sendCreatedStatus(exchange);
                        } catch (OverlapException e) {
                            sendHasOverlap(exchange);
                        }
                    }
                } else {
                    sendError(exchange, new ErrorResponse(400, "Тело запроса не в формате json"));
                }
            }
        } else {
            sendNotFoundEndpoint(exchange);
        }
    }

    private void deleteHandler(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3 && strIsTasks(pathParts[1]) && strIsId(pathParts[2])) {
            int id = Integer.parseInt(pathParts[2]);
            try {
                this.taskManager.removeTask(id);
                sendSuccessStatus(exchange, "Задача удалена");
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

    private boolean strIsTasks(String target) {
        return target.equals("tasks");
    }
}
