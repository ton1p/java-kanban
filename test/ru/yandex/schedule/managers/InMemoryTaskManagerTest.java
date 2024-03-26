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

import java.util.ArrayList;
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
        taskManager.addTask(task);
        Assertions.assertEquals(1, taskManager.getTasksList().size());
    }

    @Test
    void getEpicsList() {
        Epic epic = new Epic("t", "t");
        taskManager.addTask(epic);
        Assertions.assertEquals(1, taskManager.getEpicsList().size());
    }

    @Test
    void getSubTasksList() {
        Epic epic = new Epic("e", "e");
        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        taskManager.addTask(subTask);
        Assertions.assertEquals(1, taskManager.getSubTasksList().size());
    }

    @Test
    void removeAllTaskByType() {
        Epic epic = new Epic("e", "e");
        Task task = new Task("t", "t", Status.NEW);
        SubTask subTask = new SubTask("s", "s", Status.NEW, epic.getId());
        taskManager.addTask(epic);
        taskManager.addTask(task);
        taskManager.addTask(subTask);

        Assertions.assertEquals(1, taskManager.getEpicsList().size());
        Assertions.assertEquals(1, taskManager.getTasksList().size());
        Assertions.assertEquals(1, taskManager.getSubTasksList().size());

        taskManager.removeAllTaskByType(TaskType.TASK);
        taskManager.removeAllTaskByType(TaskType.EPIC);
        taskManager.removeAllTaskByType(TaskType.SUBTASK);

        Assertions.assertEquals(0, taskManager.getEpicsList().size());
        Assertions.assertEquals(0, taskManager.getTasksList().size());
        Assertions.assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    void getTaskById() {
        Task task = new Task("t", "t", Status.NEW);
        taskManager.addTask(task);
        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic("t", "t");
        taskManager.addTask(epic);
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void getSubTaskById() {
        Epic epic = new Epic("t", "t");
        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        taskManager.addTask(subTask);
        Assertions.assertEquals(subTask.getId(), taskManager.getSubTaskById(subTask.getId()).getId());
    }

    @Test
    void updateTask() {
        Task task = new Task("t", "t", Status.NEW);
        taskManager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
        task.setDescription("description");
        taskManager.updateTask(task);
        Assertions.assertEquals("description", taskManager.getTaskById(task.getId()).getDescription());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("t", "t");
        taskManager.addTask(epic);
        epic.setName("new name");
        taskManager.updateEpic(epic);
        Assertions.assertEquals("new name", taskManager.getEpicById(epic.getId()).getName());
        Assertions.assertEquals(epic.getName(), taskManager.getEpicById(epic.getId()).getName());
    }

    @Test
    void updateSubTask() {
        Epic epic = new Epic("t", "t");
        SubTask subTask = new SubTask("s", "s", Status.NEW, epic.getId());
        epic.addSubTask(subTask);
        taskManager.addTask(epic);
        taskManager.addTask(subTask);
        Assertions.assertNotNull(taskManager.getEpicById(epic.getId()));
        Assertions.assertNotNull(taskManager.getSubTaskById(subTask.getId()));

        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        Assertions.assertEquals(Status.DONE, taskManager.getSubTaskById(subTask.getId()).getStatus());
    }

    @Test
    void removeTask() {
        Task task = new Task("t", "t", Status.NEW);
        taskManager.addTask(task);
        Assertions.assertEquals(1, taskManager.getTasksList().size());
        taskManager.removeTask(task.getId());
        Assertions.assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    void removeEpic() {
        Epic epic = new Epic("t", "t");
        SubTask subTask = new SubTask("s", "s", Status.NEW, epic.getId());
        epic.addSubTask(subTask);

        taskManager.addTask(epic);
        taskManager.addTask(subTask);

        Assertions.assertEquals(1, taskManager.getEpicsList().size());
        taskManager.removeEpic(epic.getId());
        Assertions.assertEquals(0, taskManager.getSubTasksList().size());
        Assertions.assertEquals(0, taskManager.getEpicsList().size());
    }

    @Test
    void removeSubTask() {
        Epic epic = new Epic("t", "t");
        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        epic.addSubTask(subTask);
        taskManager.addTask(epic);
        Assertions.assertEquals(1, taskManager.getEpicsList().size());
        Assertions.assertEquals(1, taskManager.getSubTasksList().size());
        taskManager.removeSubTask(subTask.getId());
        Assertions.assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    void getEpicSubTasks() {
        List<SubTask> subTasks = new ArrayList<>();
        Epic epic = new Epic("t", "t");
        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        epic.addSubTask(subTask);
        subTasks.add(subTask);
        taskManager.addTask(epic);
        taskManager.addTask(subTask);
        Assertions.assertEquals(subTasks, taskManager.getEpicSubTasks(epic.getId()));
    }

    @Test
    void getHistory() {
        Epic epic = new Epic("t", "t");
        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        epic.addSubTask(subTask);
        Task task = new Task("t", "t", Status.NEW);

        taskManager.addTask(epic);
        taskManager.addTask(task);

        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());
        taskManager.getTaskById(task.getId());

        task.setName("updated name");
        taskManager.updateTask(task);
        taskManager.getTaskById(task.getId());

        Assertions.assertEquals(List.of(epic, subTask, task), taskManager.getHistory());

        Task anotherTask = new Task("another task", "at", Status.IN_PROGRESS);
        taskManager.addTask(anotherTask);

        taskManager.getTaskById(anotherTask.getId());

        Assertions.assertEquals(List.of(epic, subTask, task, anotherTask), taskManager.getHistory());

        taskManager.removeEpic(epic.getId());

        Assertions.assertEquals(List.of(task, anotherTask), taskManager.getHistory());

        taskManager.removeTask(task.getId());
        Assertions.assertEquals(List.of(anotherTask), taskManager.getHistory());
    }
}
