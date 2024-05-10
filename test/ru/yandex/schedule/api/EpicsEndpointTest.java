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
import ru.yandex.schedule.tasks.enums.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

class EpicsEndpointTest extends HttpTaskServerTest {
    URI uri = URI.create("http://localhost:8080/epics");

    @Test
    void getEpics() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

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
    void getEpicsAfterAddOne() throws IOException, InterruptedException {
        this.manager.addEpic(new Epic("epic", "epic desc"));

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

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
    void getEpicById() throws IOException, InterruptedException {
        this.manager.addEpic(new Epic("epic", "epic desc"));

        URI uri1 = URI.create("http://localhost:8080/epics/1");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri1)
                .GET()
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            int statusCode = jsonObject.get("statusCode").getAsInt();
            JsonObject data = jsonObject.get("data").getAsJsonObject();

            String name = data.get("name").getAsString();

            Assertions.assertEquals(200, statusCode);
            Assertions.assertEquals("epic", name);
        }
    }

    @Test
    void getEpicByIdWithWrongId() throws IOException, InterruptedException {
        URI uri1 = URI.create("http://localhost:8080/epics/100");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri1)
                .GET()
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(404, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            int statusCode = jsonObject.get("statusCode").getAsInt();
            String message = jsonObject.get("message").getAsString();

            Assertions.assertEquals(404, statusCode);
            Assertions.assertEquals("Эпик с id 100 не найден", message);
        }
    }

    @Test
    void getEpicSubTasks() throws IOException, InterruptedException, OverlapException, NotFoundException {
        Epic epic = new Epic("epic", "desc");
        this.manager.addEpic(epic);
        SubTask subTask = new SubTask("subtask", "desc", Status.IN_PROGRESS, epic.getId(), Duration.ofMinutes(5), Instant.now());
        this.manager.addSubTask(subTask);
        URI uri1 = URI.create("http://localhost:8080/epics/1/subtasks");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri1)
                .GET()
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

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
    void addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc");
        String epicJson = this.gson.toJson(epic);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        Assertions.assertEquals(1, this.manager.getEpicsList().size());
    }

    @Test
    void updateEpic() throws IOException, InterruptedException, NotFoundException {
        Epic epic = new Epic("epic", "desc");
        this.manager.addEpic(epic);

        epic = this.manager.getEpicById(1);
        epic.setName("updated");

        String epicJson = this.gson.toJson(epic);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        Assertions.assertEquals("updated", this.manager.getEpicById(1).getName());
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc");
        this.manager.addEpic(epic);

        URI uri1 = URI.create("http://localhost:8080/epics/1");

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri1)
                .DELETE()
                .build();

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertEquals("Эпик удален", response.body());
    }
}
