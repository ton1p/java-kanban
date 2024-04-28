package ru.yandex.schedule.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.tasks.enums.Status;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;
    SubTask subTask;

    @BeforeEach
    void beforeEach() {
        this.epic = new Epic("test", "test");
        this.subTask = new SubTask("test", "test", Status.NEW, epic.getId());
    }

    @AfterEach
    void afterEach() {
        this.epic.clearSubTasks();
    }

    @Test
    void getStatus() {
        assertEquals(Status.NEW, epic.getStatus());

        epic.addSubTask(subTask);
        assertEquals(Status.NEW, epic.getStatus());

        subTask.setStatus(Status.IN_PROGRESS);
        epic.updateSubTask(subTask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        subTask.setStatus(Status.DONE);
        epic.updateSubTask(subTask);
        assertEquals(Status.DONE, epic.getStatus());

        epic.clearSubTasks();
        assertEquals(Status.NEW, epic.getStatus());

        Epic epic1 = new Epic("e", "e");
        for (int i = 0; i < 3; i++) {
            SubTask subTask1 = new SubTask("s", "s", Status.NEW, epic1.getId());
            epic1.addSubTask(subTask1);
        }

        assertEquals(Status.NEW, epic1.getStatus());
        epic1.clearSubTasks();

        for (int i = 0; i < 3; i++) {
            SubTask subTask1 = new SubTask("s", "s", Status.IN_PROGRESS, epic1.getId());
            epic1.addSubTask(subTask1);
        }

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
        epic1.clearSubTasks();

        for (int i = 0; i < 3; i++) {
            SubTask subTask1 = new SubTask("s", "s", Status.DONE, epic1.getId());
            epic1.addSubTask(subTask1);
        }

        assertEquals(Status.DONE, epic1.getStatus());
        epic1.clearSubTasks();

        for (int i = 0; i < 3; i++) {
            SubTask subTask1 = new SubTask("s", "s", i % 2 == 0 ? Status.DONE : Status.NEW, epic1.getId());
            epic1.addSubTask(subTask1);
        }

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
        epic1.clearSubTasks();
    }

    @Test
    void getSubTasks() {
        epic.addSubTask(subTask);
        ArrayList<SubTask> arrayList = new ArrayList<>();
        arrayList.add(subTask);
        assertEquals(arrayList, epic.getSubTasks());
    }

    @Test
    void removeSubTask() {
        epic.addSubTask(subTask);
        epic.removeSubTask(epic.getSubTasks().get(0));
        assertEquals(0, epic.getSubTasks().size());
    }

    @Test
    void updateSubTask() {
        epic.addSubTask(subTask);
        subTask.setStatus(Status.DONE);
        epic.updateSubTask(subTask);
        assertTrue(epic.updateSubTask(subTask));
        assertEquals(Status.DONE, epic.getSubTasks().get(0).getStatus());

        SubTask subTask1 = new SubTask("t", "t", Status.NEW, epic.getId());
        assertFalse(epic.updateSubTask(subTask1));
    }

    @Test
    void equals() {
        Epic epic1 = new Epic("n", "d");
        Epic epic2 = new Epic("n", "d");

        assertEquals(epic1, epic2);

        epic1.addSubTask(new SubTask("n", "d", Status.NEW, epic1.getId()));

        assertNotEquals(epic1, epic2);
    }

    @Test
    void getDuration() {
        Epic epic1 = new Epic("n", "d");
        SubTask subTask1 = new SubTask("n", "d", Status.NEW, epic1.getId());
        epic1.addSubTask(subTask1);

        assertEquals(Duration.ZERO, epic1.getDuration());

        SubTask subTask2 = new SubTask("n", "d", Status.NEW, epic1.getId(), Duration.ofMinutes(5), Instant.now());
        epic1.addSubTask(subTask2);

        assertEquals(Duration.ofMinutes(5), epic1.getDuration());

        epic1.clearSubTasks();

        assertEquals(Duration.ZERO, epic1.getDuration());
    }

    @Test
    void getStartTime() {
        assertNull(epic.getStartTime());

        epic.addSubTask(subTask);
        assertNull(epic.getStartTime());

        Instant now = Instant.now();
        epic.addSubTask(new SubTask("n", "d", Status.NEW, epic.getId(), Duration.ofMinutes(5), now));
        assertNull(epic.getStartTime());
        epic.removeSubTask(subTask);
        assertEquals(now, epic.getStartTime());
        epic.addSubTask(new SubTask("n", "d", Status.NEW, epic.getId(), Duration.ofMinutes(5), now.plus(Duration.ofMinutes(5))));
        assertEquals(now, epic.getStartTime());
    }

    @Test
    void getEndTime() {
        Instant now = Instant.now();

        assertNull(epic.getEndTime());

        epic.addSubTask(new SubTask("n", "d", Status.NEW, epic.getId(), Duration.ofMinutes(5), now));
        assertEquals(now.plus(Duration.ofMinutes(5)), epic.getEndTime());

        epic.addSubTask(new SubTask("n", "d", Status.NEW, epic.getId(), Duration.ofMinutes(5), now.plus(Duration.ofMinutes(5))));
        assertEquals(now.plus(Duration.ofMinutes(10)), epic.getEndTime());
    }

    @Test
    void epicHashCode() {
        Epic epic1 = new Epic("e", "e");
        Epic epic2 = new Epic("e", "e");

        assertEquals(epic1.hashCode(), epic2.hashCode());

        epic2.setName("new name");

        assertNotEquals(epic1.hashCode(), epic2.hashCode());
    }
}
