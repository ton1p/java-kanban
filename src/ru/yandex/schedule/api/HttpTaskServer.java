package ru.yandex.schedule.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.schedule.api.controllers.*;
import ru.yandex.schedule.api.typeAdapters.DurationAdapter;
import ru.yandex.schedule.api.typeAdapters.InstantAdapter;
import ru.yandex.schedule.managers.Managers;
import ru.yandex.schedule.managers.interfaces.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;

public class HttpTaskServer {
    private HttpServer server;

    private final TaskManager manager;

    private final Gson gson;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .serializeNulls()
                .create();
    }

    public Gson getGson() {
        return gson;
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getInMemoryTask());
        httpTaskServer.start();
        System.out.println("Сервер запущен на " + httpTaskServer.getPort() + " порту");
    }

    public void start() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(8080), 0);
            this.server.createContext("/tasks", new TaskController(this.manager, this.gson));
            this.server.createContext("/subtasks", new SubTaskController(this.manager, this.gson));
            this.server.createContext("/epics", new EpicController(this.manager, this.gson));
            this.server.createContext("/history", new HistoryController(this.manager, this.gson));
            this.server.createContext("/prioritized", new PrioritizedController(this.manager, this.gson));
            this.server.start();
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера.");
            e.printStackTrace();
        }
    }

    public void stop() {
        this.server.stop(1);
    }

    public int getPort() {
        return this.server.getAddress().getPort();
    }
}
