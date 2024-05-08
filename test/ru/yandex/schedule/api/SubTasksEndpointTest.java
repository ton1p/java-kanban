package ru.yandex.schedule.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.managers.exceptions.NotFoundException;
import ru.yandex.schedule.managers.exceptions.OverlapException;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

class SubTasksEndpointTest extends HttpTaskServerTest {
    @Test
    void addSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic desc");
        this.manager.addEpic(epic);
        SubTask subTask = new SubTask("subTask", "subTask desc", Status.NEW, epic.getId(), Duration.ofMinutes(5), Instant.now());
        String subtaskJson = this.gson.toJson(subTask);

        URI url = URI.create(this.baseUrl + "/subtasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<SubTask> subTasksList = this.manager.getSubTasksList();

        Assertions.assertNotNull(subTasksList);
        Assertions.assertEquals(1, subTasksList.size());
        Assertions.assertEquals("subTask", subTasksList.get(0).getName());
    }

    @Test
    void updateSubTask() throws IOException, InterruptedException, NotFoundException {
        Epic epic = new Epic("epic", "epic desc");
        this.manager.addEpic(epic);
        SubTask subTask = new SubTask("subTask", "subTask desc", Status.NEW, epic.getId(), Duration.ofMinutes(5), Instant.now());
        String subtaskJson = this.gson.toJson(subTask);

        URI url = URI.create(this.baseUrl + "/subtasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<SubTask> subTasksList = this.manager.getSubTasksList();

        Assertions.assertNotNull(subTasksList);
        Assertions.assertEquals(1, subTasksList.size());
        Assertions.assertEquals("subTask", subTasksList.get(0).getName());

        // update subTask
        subTask = this.manager.getSubTaskById(2);
        subTask.setName("updated");
        subtaskJson = this.gson.toJson(subTask);
        request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("updated", subTasksList.get(0).getName());
    }

    @Test
    void updateSubTaskWithWrongId() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic desc");
        this.manager.addEpic(epic);
        SubTask subTask = new SubTask("subTask", "subTask desc", Status.NEW, epic.getId(), Duration.ofMinutes(5), Instant.now());
        subTask.setId(2);
        String subtaskJson = this.gson.toJson(subTask);

        URI url = URI.create(this.baseUrl + "/subtasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void addSubTaskWithOverlapTime() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic desc");
        this.manager.addEpic(epic);
        SubTask subtask = new SubTask("subtask", "subtask desc", Status.NEW, epic.getId(), Duration.ofMinutes(5), Instant.now());
        SubTask subtask1 = new SubTask("subtask1", "subtask1 desc", Status.NEW, epic.getId(), Duration.ofMinutes(5), Instant.now());
        String subtaskJson = this.gson.toJson(subtask);
        String subtask1Json = this.gson.toJson(subtask1);

        URI url = URI.create(this.baseUrl + "/subtasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<SubTask> subTasksList = this.manager.getSubTasksList();

        Assertions.assertNotNull(subTasksList);
        Assertions.assertEquals(1, subTasksList.size());

        request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtask1Json))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(1, subTasksList.size());
    }

    @Test
    void getSubTasks() throws IOException, InterruptedException {
        URI url = URI.create(this.baseUrl + "/subtasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            int statusCode = jsonObject.get("statusCode").getAsInt();
            JsonArray data = jsonObject.get("data").getAsJsonArray();

            Assertions.assertEquals(200, statusCode);
            Assertions.assertEquals(0, data.size());
        }
    }

    @Test
    void getSubTasksAfterAddOne() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic desc");
        this.manager.addEpic(epic);
        // add subtask
        SubTask subTask = new SubTask("subTask", "subTask desc", Status.NEW, epic.getId(), Duration.ofMinutes(5), Instant.now());
        String subtaskJson = this.gson.toJson(subTask);

        URI url = URI.create(this.baseUrl + "/subtasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        // get subtasks
        request = HttpRequest
                .newBuilder()
                .uri(url).
                GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            int statusCode = jsonObject.get("statusCode").getAsInt();
            JsonArray data = jsonObject.get("data").getAsJsonArray();

            Assertions.assertEquals(200, statusCode);
            Assertions.assertEquals(1, data.size());
        }
    }

    @Test
    void getSubTaskById() throws IOException, InterruptedException, OverlapException, NotFoundException {
        Epic epic = new Epic("epic", "epic desc");
        this.manager.addEpic(epic);
        // add subtask
        SubTask subTask = new SubTask("subTask", "subTask desc", Status.NEW, epic.getId(), Duration.ofMinutes(5), Instant.now());
        this.manager.addSubTask(subTask);

        // get subTask by id
        URI url = URI.create(this.baseUrl + "/subtasks/2");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            int statusCode = jsonObject.get("statusCode").getAsInt();
            JsonObject data = jsonObject.get("data").getAsJsonObject();

            String name = data.get("name").getAsString();

            Assertions.assertEquals(200, statusCode);
            Assertions.assertEquals("subTask", name);
        }
    }

    @Test
    void getSubTaskByIdWithWrongId() throws IOException, InterruptedException {
        // get subtask by id
        URI url = URI.create(this.baseUrl + "/subtasks/100");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            int statusCode = jsonObject.get("statusCode").getAsInt();
            String message = jsonObject.get("message").getAsString();

            Assertions.assertEquals(404, statusCode);
            Assertions.assertEquals("Подзадача с id 100 не найдена", message);
        }
    }

    @Test
    void deleteSubTask() throws IOException, InterruptedException, OverlapException, NotFoundException {
        Epic epic = new Epic("epic", "epic desc");
        this.manager.addEpic(epic);
        this.manager.addSubTask(new SubTask("subtask", "desc", Status.NEW, epic.getId(), Duration.ofMinutes(5), Instant.now()));
        URI url = URI.create(this.baseUrl + "/subtasks/2");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Подзадача удалена", response.body());
    }
}
