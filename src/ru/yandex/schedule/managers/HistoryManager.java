package ru.yandex.schedule.managers;

import ru.yandex.schedule.tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {
    ArrayList<Task> getHistory();

    void add(Task task);
}
