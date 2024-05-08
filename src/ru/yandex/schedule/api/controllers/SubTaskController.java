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
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.enums.Status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class SubTaskController extends BaseController {
    public SubTaskController(TaskManager taskManager, Gson gson) {
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
        if (pathParts.length == 2 && strIsSubTasks(pathParts[1])) {
            List<SubTask> subTaskList = this.taskManager.getSubTasksList();
            sendResponse(exchange, subTaskList);
        } else if (pathParts.length == 3 && strIsSubTasks(pathParts[1]) && strIsId(pathParts[2])) {
            int id = Integer.parseInt(pathParts[2]);
            try {
                sendResponse(exchange, this.taskManager.getSubTaskById(id));
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        } else {
            sendNotFoundEndpoint(exchange);
        }
    }

    private void postHandle(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2 && strIsSubTasks(pathParts[1])) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(body);

            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement id = jsonObject.get("id");
                JsonElement name = jsonObject.get("name");
                JsonElement description = jsonObject.get("description");
                JsonElement status = jsonObject.get("status");
                JsonElement duration = jsonObject.get("duration");
                JsonElement startTime = jsonObject.get("startTime");
                JsonElement epicId = jsonObject.get("epicId");

                if (name == null || description == null || status == null || epicId == null) {
                    sendError(exchange, new ErrorResponse(400, "Не заполнены обязательные поля"));
                } else {
                    String nameValue = name.getAsString();
                    String descriptionValue = description.getAsString();
                    Status statusValue = Status.getStatusByString(status.getAsString());
                    int epicIdValue = epicId.getAsInt();

                    SubTask subTask = new SubTask(nameValue, descriptionValue, statusValue, epicIdValue);

                    if (duration != null && startTime != null) {
                        Duration durationValue = Duration.ofMinutes(duration.getAsInt());
                        Instant startTimeValue = Instant.parse(startTime.getAsString());
                        subTask.setDuration(durationValue);
                        subTask.setStartTime(startTimeValue);
                    }

                    try {
                        if (id != null && id.getAsInt() != 0) {
                            subTask.setId(id.getAsInt());
                            this.taskManager.updateSubTask(subTask);
                        } else {
                            this.taskManager.addSubTask(subTask);
                        }
                        sendCreatedStatus(exchange);
                    } catch (OverlapException e) {
                        sendHasOverlap(exchange);
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
        if (pathParts.length == 3 && strIsSubTasks(pathParts[1]) && strIsId(pathParts[2])) {
            int id = Integer.parseInt(pathParts[2]);
            try {
                this.taskManager.removeSubTask(id);
                sendSuccessStatus(exchange, "Подзадача удалена");
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

    private boolean strIsSubTasks(String target) {
        return target.equals("subtasks");
    }
}
