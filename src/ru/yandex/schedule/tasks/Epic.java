package ru.yandex.schedule.tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;

    private Status status;

    public Status getStatus() {
        return this.status;
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasks = new ArrayList<>();
        this.status = Status.NEW;
    }

    public ArrayList<SubTask> getSubTasks() {
        return this.subTasks;
    }

    public void removeSubTask(SubTask subTask) {
        this.subTasks.remove(subTask);
        this.status = this.computeStatus();
    }

    public boolean updateSubTask(SubTask subTask) {
        int index = this.subTasks.indexOf(subTask);
        if (index != -1) {
            this.subTasks.set(index, subTask);
            this.status = this.computeStatus();
            return true;
        }
        return false;
    }

    public void addSubTask(SubTask subTask) {
        this.subTasks.add(subTask);
        this.status = this.computeStatus();
    }

    public void clearSubTasks() {
        this.subTasks.clear();
        this.status = this.computeStatus();
    }

    private Status computeStatus() {
        int doneSize = 0;
        int newSize = 0;

        if (subTasks.isEmpty()) {
            return Status.NEW;
        }

        for (SubTask subTask : subTasks) {
            if (subTask.status == Status.DONE) {
                doneSize++;
                continue;
            }
            if (subTask.status == Status.NEW) {
                newSize++;
            }
        }

        if (doneSize == subTasks.size()) {
            return Status.DONE;
        }

        if (newSize == subTasks.size()) {
            return  Status.NEW;
        }

        return Status.IN_PROGRESS;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksSize=" + subTasks.size() +
                ", status=" + status +
                '}';
    }
}
