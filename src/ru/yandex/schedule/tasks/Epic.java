package ru.yandex.schedule.tasks;

import ru.yandex.schedule.tasks.enums.Status;
import ru.yandex.schedule.tasks.enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return this.subTasks;
    }

    public void removeSubTask(SubTask subTask) {
        int index = this.subTasks.indexOf(subTask);
        if (index != -1) {
            this.subTasks.remove(index);
            this.setStatus(this.computeStatus());
        }
    }

    public boolean updateSubTask(SubTask subTask) {
        int index = this.subTasks.indexOf(subTask);
        if (index != -1) {
            this.subTasks.set(index, subTask);
            this.setStatus(this.computeStatus());
            return true;
        }
        return false;
    }

    public void addSubTask(SubTask subTask) {
        this.subTasks.add(subTask);
        this.setStatus(this.computeStatus());
    }

    public void clearSubTasks() {
        this.subTasks.clear();
        this.setStatus(this.computeStatus());
    }

    private Status computeStatus() {
        int doneSize = 0;
        int newSize = 0;

        if (subTasks.isEmpty()) {
            return Status.NEW;
        }

        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == Status.DONE) {
                doneSize++;
                continue;
            }
            if (subTask.getStatus() == Status.NEW) {
                newSize++;
            }
        }

        if (doneSize == subTasks.size()) {
            return Status.DONE;
        }

        if (newSize == subTasks.size()) {
            return Status.NEW;
        }

        return Status.IN_PROGRESS;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s", getId(), TaskType.EPIC, getName(), getStatus(), getDescription());
    }
}
