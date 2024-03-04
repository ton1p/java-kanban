package ru.yandex.schedule.managers;

import ru.yandex.schedule.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return this.history;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (this.history.size() < 10) {
                this.history.add(task);
            } else {
                this.history.remove(0);
                this.history.add(task);
            }
        }
    }
}
