package ru.yandex.schedule.tasks;

import ru.yandex.schedule.tasks.enums.Status;
import ru.yandex.schedule.tasks.enums.TaskType;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        Optional<SubTask> subTaskFounded = this.subTasks.stream().filter(s -> s.getId() == subTask.getId()).findFirst();
        if (subTaskFounded.isPresent()) {
            int index = this.subTasks.indexOf(subTaskFounded.get());
            if (index != -1) {
                this.subTasks.set(index, subTask);
                this.setStatus(this.computeStatus());
                return true;
            }
            return false;
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
    public Duration getDuration() {
        long minutes = 0;
        if (!this.subTasks.isEmpty()) {
            minutes = this.subTasks
                    .stream()
                    .filter(subTask -> subTask.getDuration() != null)
                    .reduce(0L, (aLong, subTask) -> aLong + subTask.getDuration().toMinutes(), Long::sum);
        }
        Duration duration = Duration.ofMinutes(minutes);
        this.setDuration(duration);
        return duration;
    }

    @Override
    public Instant getStartTime() {
        if (!this.subTasks.isEmpty()) {
            Instant startTime = this.subTasks.get(0).getStartTime();
            this.setStartTime(startTime);
            return startTime;
        }
        return null;
    }

    @Override
    public Instant getEndTime() {
        if (!this.subTasks.isEmpty()) {
            return this.subTasks.get(this.subTasks.size() - 1).getEndTime();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasks, epic.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks);
    }

    @Override
    public String toString() {
        String duration = getDuration().toMinutes() == 0 ? "null" : getDuration().toMinutes() + "";
        String startTime = getStartTime() != null ? getStartTime().toString() : "null";
        return String.format("%s,%s,%s,%s,%s,%s,%s", getId(), TaskType.EPIC, getName(), getStatus(), getDescription(), duration, startTime);
    }
}
