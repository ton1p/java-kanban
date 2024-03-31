package ru.yandex.schedule.managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.schedule.managers.interfaces.TaskManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;
import ru.yandex.schedule.tasks.enums.TaskType;

import java.util.List;

class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        this.taskManager = Managers.getDefaultTask();
    }

    @Test
    void getTasksList() {
        Task task = new Task("t", "t", Status.NEW);
        this.taskManager.addTask(task);
        Assertions.assertEquals(1, this.taskManager.getTasksList().size());
    }

    @Test
    void getEpicsList() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        Assertions.assertEquals(1, this.taskManager.getEpicsList().size());
    }

    @Test
    void getSubTasksList() {
        Epic epic = new Epic("e", "e");
        this.taskManager.addEpic(epic);
        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);
        Assertions.assertEquals(1, this.taskManager.getSubTasksList().size());
    }

    @Test
    void removeAllTaskByType() {
        Epic epic = new Epic("e", "e");
        this.taskManager.addEpic(epic);
        Task task = new Task("t", "t", Status.NEW);
        this.taskManager.addTask(task);
        SubTask subTask = new SubTask("s", "s", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);

        Assertions.assertEquals(1, this.taskManager.getEpicsList().size());
        Assertions.assertEquals(1, this.taskManager.getTasksList().size());
        Assertions.assertEquals(1, this.taskManager.getSubTasksList().size());

        this.taskManager.removeAllTaskByType(TaskType.TASK);
        this.taskManager.removeAllTaskByType(TaskType.EPIC);
        this.taskManager.removeAllTaskByType(TaskType.SUBTASK);

        Assertions.assertEquals(0, this.taskManager.getEpicsList().size());
        Assertions.assertEquals(0, this.taskManager.getTasksList().size());
        Assertions.assertEquals(0, this.taskManager.getSubTasksList().size());

        Task task1 = new Task("", "", Status.NEW);
        this.taskManager.addTask(task1);
        Task task2 = new Task("", "", Status.NEW);
        this.taskManager.addTask(task2);
        this.taskManager.getTaskById(task1.getId());
        this.taskManager.getTaskById(task2.getId());
        Assertions.assertEquals(List.of(task1, task2), this.taskManager.getHistory());
        this.taskManager.removeAllTaskByType(TaskType.TASK);
        Assertions.assertEquals(0, this.taskManager.getHistory().size());
    }

    @Test
    void getTaskById() {
        Task task = new Task("t", "t", Status.NEW);
        this.taskManager.addTask(task);
        Assertions.assertEquals(task, this.taskManager.getTaskById(task.getId()));
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        Assertions.assertEquals(epic, this.taskManager.getEpicById(epic.getId()));
    }

    @Test
    void getSubTaskById() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);
        Assertions.assertEquals(subTask.getId(), this.taskManager.getSubTaskById(subTask.getId()).getId());
    }

    @Test
    void updateTask() {
        Task task = new Task("t", "t", Status.NEW);
        this.taskManager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        this.taskManager.updateTask(task);
        Assertions.assertEquals(Status.IN_PROGRESS, this.taskManager.getTaskById(task.getId()).getStatus());
        task.setDescription("description");
        this.taskManager.updateTask(task);
        Assertions.assertEquals("description", this.taskManager.getTaskById(task.getId()).getDescription());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        epic.setName("new name");
        this.taskManager.updateEpic(epic);
        Assertions.assertEquals("new name", this.taskManager.getEpicById(epic.getId()).getName());
        Assertions.assertEquals(epic.getName(), this.taskManager.getEpicById(epic.getId()).getName());
    }

    @Test
    void updateSubTask() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "s", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);
        Assertions.assertNotNull(this.taskManager.getEpicById(epic.getId()));
        Assertions.assertNotNull(this.taskManager.getSubTaskById(subTask.getId()));

        subTask.setStatus(Status.DONE);
        this.taskManager.updateSubTask(subTask);
        Assertions.assertEquals(Status.DONE, this.taskManager.getSubTaskById(subTask.getId()).getStatus());
    }

    @Test
    void removeTask() {
        Task task = new Task("t", "t", Status.NEW);
        this.taskManager.addTask(task);
        Assertions.assertEquals(1, this.taskManager.getTasksList().size());
        this.taskManager.removeTask(task.getId());
        Assertions.assertEquals(0, this.taskManager.getTasksList().size());
    }

    @Test
    void removeEpic() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "s", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);

        Assertions.assertEquals(1, this.taskManager.getEpicsList().size());
        Assertions.assertEquals(1, this.taskManager.getSubTasksList().size());
        this.taskManager.removeEpic(epic.getId());
        Assertions.assertEquals(0, this.taskManager.getEpicsList().size());
        Assertions.assertEquals(0, this.taskManager.getSubTasksList().size());
    }

    @Test
    void removeSubTask() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);
        Assertions.assertEquals(1, this.taskManager.getEpicsList().size());
        Assertions.assertEquals(1, this.taskManager.getSubTasksList().size());
        this.taskManager.removeSubTask(subTask.getId());
        Assertions.assertEquals(0, this.taskManager.getSubTasksList().size());
    }

    @Test
    void getEpicSubTasks() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);
        Assertions.assertEquals(List.of(subTask), this.taskManager.getEpicSubTasks(epic.getId()));
    }

    @Test
    void getHistory() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);
        Task task = new Task("t", "t", Status.NEW);
        this.taskManager.addTask(task);


        this.taskManager.getEpicById(epic.getId());
        this.taskManager.getSubTaskById(subTask.getId());
        this.taskManager.getTaskById(task.getId());

        task.setName("updated name");
        this.taskManager.updateTask(task);
        this.taskManager.getTaskById(task.getId());

        Assertions.assertEquals(List.of(epic, subTask, task), this.taskManager.getHistory());

        Task anotherTask = new Task("another task", "at", Status.IN_PROGRESS);
        this.taskManager.addTask(anotherTask);

        this.taskManager.getTaskById(anotherTask.getId());

        Assertions.assertEquals(List.of(epic, subTask, task, anotherTask), this.taskManager.getHistory());

        this.taskManager.removeEpic(epic.getId());

        Assertions.assertEquals(List.of(task, anotherTask), this.taskManager.getHistory());

        this.taskManager.removeTask(task.getId());
        Assertions.assertEquals(List.of(anotherTask), this.taskManager.getHistory());
    }
}
