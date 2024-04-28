package ru.yandex.schedule.utils;

import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;

import java.time.Duration;
import java.time.Instant;

public class TaskParser {
    private TaskParser() {
        throw new IllegalStateException("Utility class");
    }

    public static Task stringToTask(String[] split) {
        int id = Integer.parseInt(split[0]);
        String name = split[2];
        Status status = Status.valueOf(split[3].toUpperCase());
        String description = split[4];
        Duration duration = split[5].equals("null") ? null : Duration.ofMinutes(Long.parseLong(split[5]));
        Instant startTime = split[6].equals("null") ? null : Instant.parse(split[6]);

        Task task;

        if (duration != null && startTime != null) {
            task = new Task(name, description, status, duration, startTime);
        } else {
            task = new Task(name, description, status);
        }

        task.setId(id);

        return task;
    }

    public static SubTask stringToSubtask(String[] split) {
        int id = Integer.parseInt(split[0]);
        String name = split[2];
        Status status = Status.valueOf(split[3].toUpperCase());
        String description = split[4];
        Duration duration = split[5].equals("null") ? null : Duration.ofMinutes(Long.parseLong(split[5]));
        Instant startTime = split[6].equals("null") ? null : Instant.parse(split[6]);
        int epicId = Integer.parseInt(split[7]);

        SubTask subTask;

        if (duration != null && startTime != null) {
            subTask = new SubTask(name, description, status, epicId, duration, startTime);
        } else {
            subTask = new SubTask(name, description, status, epicId);
        }

        subTask.setId(id);

        return subTask;
    }

    public static Epic stringToEpic(String[] split) {
        int id = Integer.parseInt(split[0]);
        String name = split[2];
        Status status = Status.valueOf(split[3].toUpperCase());
        String description = split[4];
        Duration duration = split[5].equals("null") ? null : Duration.ofMinutes(Long.parseLong(split[5]));
        Instant startTime = split[6].equals("null") ? null : Instant.parse(split[6]);

        Epic epic = new Epic(name, description);

        if (duration != null && startTime != null) {
            epic.setDuration(duration);
            epic.setStartTime(startTime);
        }

        epic.setId(id);
        epic.setStatus(status);

        return epic;
    }
}
