package ru.yandex.schedule.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest extends TaskMangerTest<InMemoryTaskManager> {
    InMemoryTaskManager taskManager;

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    void beforeEach() {
        this.taskManager = createTaskManager();
    }

    @Test
    void isTasksOverlap() {
        Instant now = Instant.now();
        Task task = new Task("d", "d", Status.NEW, Duration.ofMinutes(5), now);
        Task task1 = new Task("d", "d", Status.NEW, Duration.ofMinutes(5), now);

        assertTrue(this.taskManager.isTasksOverlap(task, task1));

        task.setDuration(Duration.ofMinutes(10));
        assertTrue(this.taskManager.isTasksOverlap(task, task1));

        task.setStartTime(now.plus(Duration.ofMinutes(5)));
        assertFalse(this.taskManager.isTasksOverlap(task, task1));
    }
}
