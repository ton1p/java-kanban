package ru.yandex.schedule.managers;

import ru.yandex.schedule.managers.interfaces.HistoryManager;
import ru.yandex.schedule.managers.interfaces.TaskManager;

import java.io.File;

public class Managers {

    private Managers() {
        throw new IllegalStateException("Utility class");
    }

    public static TaskManager getDefaultTask(File file) {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
