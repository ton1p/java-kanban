package ru.yandex.schedule.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        subTask.status = Status.IN_PROGRESS;
        epic.updateSubTask(subTask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        subTask.status = Status.DONE;
        epic.updateSubTask(subTask);
        assertEquals(Status.DONE, epic.getStatus());

        epic.clearSubTasks();
        assertEquals(Status.NEW, epic.getStatus());
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
        subTask.status = Status.DONE;
        epic.updateSubTask(subTask);
        assertTrue(epic.updateSubTask(subTask));
        assertEquals(Status.DONE, epic.getSubTasks().get(0).status);

        SubTask subTask1 = new SubTask("t", "t", Status.NEW, epic.getId());
        assertFalse(epic.updateSubTask(subTask1));
    }
}
