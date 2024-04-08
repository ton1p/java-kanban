package ru.yandex.schedule.managers;

import ru.yandex.schedule.managers.interfaces.HistoryManager;
import ru.yandex.schedule.managers.interfaces.TaskManager;

import java.io.File;

public class Managers {

    private Managers() {
        throw new IllegalStateException("Utility class");
    }

    public static TaskManager getDefaultTask() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFile(File file) {
        return new FileBackedTaskManager(file);
    }
}
