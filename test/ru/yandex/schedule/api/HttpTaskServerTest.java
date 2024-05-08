package ru.yandex.schedule.api;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.schedule.managers.InMemoryTaskManager;
import ru.yandex.schedule.managers.interfaces.TaskManager;
import ru.yandex.schedule.tasks.enums.TaskType;

import java.net.URI;
import java.net.http.HttpClient;

class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();

    HttpTaskServer server = new HttpTaskServer(manager);

    Gson gson = this.server.getGson();

    HttpClient client = HttpClient.newHttpClient();

    URI baseUrl = URI.create("http://localhost:8080");

    @BeforeEach
    protected void beforeEach() {
        this.manager.removeAllTaskByType(TaskType.TASK);
        this.manager.removeAllTaskByType(TaskType.SUBTASK);
        this.manager.removeAllTaskByType(TaskType.EPIC);
        this.server.start();
    }

    @AfterEach
    protected void afterEach() {
        this.server.stop();
    }
}
