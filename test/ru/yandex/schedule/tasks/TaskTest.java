package ru.yandex.schedule.tasks;

import org.junit.jupiter.api.Test;
import ru.yandex.schedule.tasks.enums.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class TaskTest {

    @Test
    void shouldBeCorrectStatus() {
        Task task = new Task("test", "test", Status.NEW);
        assertEquals(task.getStatus(), Status.NEW);
        task.setStatus(Status.IN_PROGRESS);
        assertEquals(task.getStatus(), Status.IN_PROGRESS);
        task.setStatus(Status.DONE);
        assertEquals(task.getStatus(), Status.DONE);
    }

    @Test
    void copy() {
        Task task = new Task("test", "test", Status.NEW);
        Task copy = new Task(task);
        assertNotSame(task, copy);
    }

    @Test
    void shouldBeCorrectIdAfterSetIdIsCalled() {
        Task task = new Task("test", "test", Status.NEW);
        task.setId("1");
        assertEquals("1", task.getId());
    }

    @Test
    void shouldBeEqualsWithTheSameIds() {
        Task task = new Task("test", "test", Status.NEW);
        Task task1 = new Task("test", "test", Status.NEW);
        task1.setId(task.getId());
        assertEquals(task, task1);
    }
}
