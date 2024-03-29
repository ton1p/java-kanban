package ru.yandex.schedule.managers;

import ru.yandex.schedule.tasks.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);
}
