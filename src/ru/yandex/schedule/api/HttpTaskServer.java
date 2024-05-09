package ru.yandex.schedule.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.schedule.api.controllers.*;
import ru.yandex.schedule.api.type_adapters.DurationAdapter;
import ru.yandex.schedule.api.type_adapters.InstantAdapter;
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
        gson = new GsonBuilder()
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
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TaskController(manager, gson));
            server.createContext("/subtasks", new SubTaskController(manager, gson));
            server.createContext("/epics", new EpicController(manager, gson));
            server.createContext("/history", new HistoryController(manager, gson));
            server.createContext("/prioritized", new PrioritizedController(manager, gson));
            server.start();
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера.");
            e.printStackTrace();
        }
    }

    public void stop() {
        server.stop(1);
    }

    public int getPort() {
        return server.getAddress().getPort();
    }
}
