package ru.yandex.schedule.managers;

public class Managers {
    public static TaskManager getDefaultTask() {
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
