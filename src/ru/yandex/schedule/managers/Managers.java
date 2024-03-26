package ru.yandex.schedule.managers;

import ru.yandex.schedule.managers.interfaces.HistoryManager;
import ru.yandex.schedule.managers.interfaces.TaskManager;

public class Managers {
    public static TaskManager getDefaultTask() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
