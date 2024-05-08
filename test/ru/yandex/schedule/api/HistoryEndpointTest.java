package ru.yandex.schedule.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

class HistoryEndpointTest extends HttpTaskServerTest {
    URI uri = URI.create("http://localhost:8080/history");

    @Test
    void getHistory() throws NotFoundException, OverlapException, IOException, InterruptedException {
        Task task = new Task("task", "desc", Status.NEW, Duration.ofMinutes(5), Instant.now());
        this.manager.addTask(task);
        this.manager.getTaskById(1);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

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
}
