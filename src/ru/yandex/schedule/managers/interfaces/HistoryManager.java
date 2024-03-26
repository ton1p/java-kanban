package ru.yandex.schedule.managers.interfaces;

import ru.yandex.schedule.tasks.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);
}
