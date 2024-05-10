package ru.yandex.schedule.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.api.models.SuccessResponse;
import ru.yandex.schedule.managers.exceptions.NotFoundException;
import ru.yandex.schedule.managers.exceptions.OverlapException;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

class TasksEndpointTest extends HttpTaskServerTest {
    @Test
    void addTask() throws IOException, InterruptedException {
        Task task = new Task("task", "task desc", Status.NEW, Duration.ofMinutes(5), Instant.now());
        String taskJson = this.gson.toJson(task);

        URI url = URI.create(this.baseUrl + "/tasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Task> taskList = this.manager.getTasksList();

        Assertions.assertNotNull(taskList);
        Assertions.assertEquals(1, taskList.size());
        Assertions.assertEquals("task", taskList.get(0).getName());
    }

    @Test
    void updateTask() throws IOException, InterruptedException, NotFoundException {
        Task task = new Task("task", "task desc", Status.NEW, Duration.ofMinutes(5), Instant.now());
        String taskJson = this.gson.toJson(task);

        URI url = URI.create(this.baseUrl + "/tasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Task> taskList = this.manager.getTasksList();

        Assertions.assertNotNull(taskList);
        Assertions.assertEquals(1, taskList.size());
        Assertions.assertEquals("task", taskList.get(0).getName());

        // update task
        task = this.manager.getTaskById(1);
        task.setName("updated");
        taskJson = this.gson.toJson(task);
        request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("updated", taskList.get(0).getName());
    }

    @Test
    void updateTaskWithWrongId() throws IOException, InterruptedException, NotFoundException {
        Task task = new Task("task", "task desc", Status.NEW, Duration.ofMinutes(5), Instant.now());
        task.setId(1);
        String taskJson = this.gson.toJson(task);

        URI url = URI.create(this.baseUrl + "/tasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());

        List<Task> taskList = this.manager.getTasksList();

        Assertions.assertNotNull(taskList);
        Assertions.assertEquals(0, taskList.size());
    }

    @Test
    void addTaskWithOverlapTime() throws IOException, InterruptedException {
        Task task = new Task("task", "task desc", Status.NEW, Duration.ofMinutes(5), Instant.now());
        Task task1 = new Task("task", "task desc", Status.NEW, Duration.ofMinutes(5), Instant.now());
        String taskJson = this.gson.toJson(task);
        String task1Json = this.gson.toJson(task1);

        URI url = URI.create(this.baseUrl + "/tasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Task> taskList = this.manager.getTasksList();

        Assertions.assertNotNull(taskList);
        Assertions.assertEquals(1, taskList.size());

        request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
        Assertions.assertEquals(1, taskList.size());
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        URI url = URI.create(this.baseUrl + "/tasks");

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
    void getTasksAfterAddOne() throws IOException, InterruptedException {
        // add task
        Task task = new Task("task", "task desc", Status.NEW, Duration.ofMinutes(5), Instant.now());
        String taskJson = this.gson.toJson(task);

        URI url = URI.create(this.baseUrl + "/tasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        // get tasks
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
    void getTaskById() throws IOException, InterruptedException {
        // add task
        Task task = new Task("task", "task desc", Status.NEW, Duration.ofMinutes(5), Instant.now());
        String taskJson = this.gson.toJson(task);

        URI url = URI.create(this.baseUrl + "/tasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        // get task by id
        url = URI.create(this.baseUrl + "/tasks/1");

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
            JsonObject data = jsonObject.get("data").getAsJsonObject();

            String name = data.get("name").getAsString();

            Assertions.assertEquals(200, statusCode);
            Assertions.assertEquals("task", name);
        }
    }

    @Test
    void getTaskByIdWithWrongId() throws IOException, InterruptedException {
        // get task by id
        URI url = URI.create(this.baseUrl + "/tasks/1");

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
            Assertions.assertEquals("Задача с id 1 не найдена", message);
        }
    }

    @Test
    void deleteTask() throws IOException, InterruptedException, OverlapException {
        this.manager.addTask(new Task("task", "desc", Status.NEW, Duration.ofMinutes(5), Instant.now()));
        URI url = URI.create(this.baseUrl + "/tasks/1");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url).
                DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("Задача удалена", response.body());
    }
}
