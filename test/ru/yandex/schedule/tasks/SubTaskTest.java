package ru.yandex.schedule.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.tasks.enums.Status;

class SubTaskTest {

    @Test
    void testEquals() {
        Epic epic = new Epic("e", "e");
        SubTask subTask = new SubTask("t", "t", Status.IN_PROGRESS, epic.getId());
        SubTask subTask1 = new SubTask("t", "t", Status.IN_PROGRESS, epic.getId());
        subTask1.setId(subTask.getId());

        Assertions.assertEquals(subTask, subTask1);
    }

    @Test
    void setEpicId() {
        Epic epic = new Epic("e", "e");
        SubTask subTask = new SubTask("t", "t", Status.IN_PROGRESS, epic.getId());
        subTask.setEpicId("1");
        Assertions.assertEquals("1", subTask.getEpicId());
    }
}
