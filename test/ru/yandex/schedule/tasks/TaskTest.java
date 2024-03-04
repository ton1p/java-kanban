package ru.yandex.schedule.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void shouldBeCorrectStatus() {
        Task task = new Task("test", "test", Status.NEW);
        assertEquals(task.status, Status.NEW);
        task.status = Status.IN_PROGRESS;
        assertEquals(task.status, Status.IN_PROGRESS);
        task.status = Status.DONE;
        assertEquals(task.status, Status.DONE);
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
