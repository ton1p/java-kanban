package ru.yandex.schedule.managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

class InMemoryHistoryManagerTest {

    private static InMemoryHistoryManager historyManager;

    @BeforeAll
    public static void beforeAll() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldCorrectAddRemoveAndReturnHistory() {
        int TASK_ID = 1;
        int EPIC_ID = 2;
        int SUBTASK_ID = 3;

        Assertions.assertEquals(0, historyManager.getHistory().size());

        Task task = new Task("t", "t", Status.NEW, Duration.ofMinutes(5), Instant.now());
        task.setId(TASK_ID);

        Epic epic = new Epic("e", "e");
        epic.setId(EPIC_ID);

        SubTask subTask = new SubTask(
                "t",
                "t",
                Status.IN_PROGRESS,
                epic.getId(),
                Duration.ofMinutes(10),
                Instant.now().plus(Duration.ofMinutes(10))
        );
        subTask.setId(SUBTASK_ID);

        historyManager.add(task);
        Assertions.assertEquals(List.of(task), historyManager.getHistory());

        historyManager.add(subTask);
        Assertions.assertEquals(List.of(task, subTask), historyManager.getHistory());

        historyManager.add(epic);
        Assertions.assertEquals(List.of(task, subTask, epic), historyManager.getHistory());

        // duplication
        historyManager.add(task);
        Assertions.assertEquals(List.of(subTask, epic, task), historyManager.getHistory());

        // remove from start
        historyManager.remove(SUBTASK_ID);
        Assertions.assertEquals(List.of(epic, task), historyManager.getHistory());

        historyManager.add(subTask);
        Assertions.assertEquals(List.of(epic, task, subTask), historyManager.getHistory());

        // remove from mid
        historyManager.remove(TASK_ID);
        Assertions.assertEquals(List.of(epic, subTask), historyManager.getHistory());

        historyManager.add(task);
        Assertions.assertEquals(List.of(epic, subTask, task), historyManager.getHistory());

        // remove from end
        historyManager.remove(TASK_ID);
        Assertions.assertEquals(List.of(epic, subTask), historyManager.getHistory());
    }
}
