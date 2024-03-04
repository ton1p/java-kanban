package ru.yandex.schedule.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubTaskTest {

    @Test
    void testEquals() {
        Epic epic = new Epic("e", "e");
        SubTask subTask = new SubTask("t", "t", Status.IN_PROGRESS, epic.getId());
        SubTask subTask1 = new SubTask("t", "t", Status.IN_PROGRESS, epic.getId());
        subTask1.setId(subTask.getId());

        Assertions.assertEquals(subTask, subTask1);
    }
}
