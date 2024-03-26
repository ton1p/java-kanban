package ru.yandex.schedule.managers;

import ru.yandex.schedule.managers.interfaces.HistoryManager;
import ru.yandex.schedule.tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new LinkedList<>();
    }

    @Override
    public List<Task> getHistory() {
        return this.history;
    }

    @Override
    public void add(Task task) {
        if (this.history.size() == 10) {
            this.history.removeFirst();
        }
        this.history.addLast(task);
    }
}
