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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskMangerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void init() {
        this.taskManager = createTaskManager();
    }

    @Test
    public void getTasksList() {
        List<Task> taskList = this.taskManager.getTasksList();
        assertEquals(0, taskList.size());

        Task task = new Task("t", "t", Status.NEW, Duration.ofMinutes(1), Instant.now());
        this.taskManager.addTask(task);
        taskList = this.taskManager.getTasksList();

        assertEquals(1, taskList.size());
    }

    @Test
    public void getEpicsList() {
        assertEquals(0, this.taskManager.getEpicsList().size());
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        assertEquals(1, this.taskManager.getEpicsList().size());
    }

    @Test
    public void getSubTasksList() {
        assertEquals(0, this.taskManager.getSubTasksList().size());

        Epic epic = new Epic("e", "e");
        this.taskManager.addEpic(epic);

        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);

        assertEquals(1, this.taskManager.getSubTasksList().size());
    }

    @Test
    public void removeAllTaskByType() {
        Epic epic = new Epic("e", "e");
        this.taskManager.addEpic(epic);

        Task task = new Task("t", "t", Status.NEW);
        this.taskManager.addTask(task);

        SubTask subTask = new SubTask("s", "s", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);

        assertEquals(1, this.taskManager.getEpicsList().size());
        assertEquals(1, this.taskManager.getTasksList().size());
        assertEquals(1, this.taskManager.getSubTasksList().size());

        this.taskManager.removeAllTaskByType(TaskType.TASK);
        this.taskManager.removeAllTaskByType(TaskType.EPIC);
        this.taskManager.removeAllTaskByType(TaskType.SUBTASK);

        assertEquals(0, this.taskManager.getEpicsList().size());
        assertEquals(0, this.taskManager.getTasksList().size());
        assertEquals(0, this.taskManager.getSubTasksList().size());

        Task task1 = new Task("", "", Status.NEW);
        this.taskManager.addTask(task1);

        Task task2 = new Task("", "", Status.NEW);
        this.taskManager.addTask(task2);

        this.taskManager.getTaskById(task1.getId());
        this.taskManager.getTaskById(task2.getId());

        assertEquals(List.of(task1, task2), this.taskManager.getHistory());
        this.taskManager.removeAllTaskByType(TaskType.TASK);
        assertEquals(0, this.taskManager.getHistory().size());
    }

    @Test
    public void getTaskById() {
        Task task = new Task("t", "t", Status.NEW);
        this.taskManager.addTask(task);
        assertEquals(task, this.taskManager.getTaskById(task.getId()));

        this.taskManager.removeTask(task.getId());
        assertNull(this.taskManager.getTaskById(task.getId()));
    }

    @Test
    public void getEpicById() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        assertEquals(epic, this.taskManager.getEpicById(epic.getId()));

        this.taskManager.removeEpic(epic.getId());
        assertNull(this.taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void getSubTaskById() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);

        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);

        assertEquals(subTask, this.taskManager.getSubTaskById(subTask.getId()));

        this.taskManager.removeSubTask(subTask.getId());
        assertNull(this.taskManager.getSubTaskById(subTask.getId()));
    }

    @Test
    public void addTask() {
        Task task = new Task("d", "d", Status.NEW, Duration.ofMinutes(1), Instant.now());
        this.taskManager.addTask(task);

        assertEquals(task, this.taskManager.getTaskById(task.getId()));

        this.taskManager.removeAllTaskByType(TaskType.TASK);

        this.taskManager.addTask(null);
        assertEquals(0, this.taskManager.getTasksList().size());
    }

    @Test
    public void addEpic() {
        Epic epic = new Epic("d", "d");
        this.taskManager.addEpic(epic);

        assertEquals(epic, this.taskManager.getEpicById(epic.getId()));

        this.taskManager.removeAllTaskByType(TaskType.EPIC);

        this.taskManager.addEpic(null);
        assertEquals(0, this.taskManager.getEpicsList().size());
    }

    @Test
    public void addSubTask() {
        SubTask subTask = new SubTask("d", "d", Status.NEW, 1, Duration.ofMinutes(1), Instant.now());
        this.taskManager.addSubTask(subTask);
        assertEquals(0, this.taskManager.getSubTasksList().size());

        Epic epic = new Epic("d", "d");
        this.taskManager.addEpic(epic);

        subTask.setEpicId(epic.getId());
        this.taskManager.addSubTask(subTask);

        assertEquals(subTask, this.taskManager.getSubTaskById(subTask.getId()));

        this.taskManager.removeAllTaskByType(TaskType.SUBTASK);

        this.taskManager.addSubTask(null);
        assertEquals(0, this.taskManager.getSubTasksList().size());
    }

    @Test
    public void updateTask() {
        Task task = new Task("t", "t", Status.NEW);
        this.taskManager.addTask(task);

        task.setStatus(Status.IN_PROGRESS);
        this.taskManager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, this.taskManager.getTaskById(task.getId()).getStatus());

        task.setDescription("description");
        this.taskManager.updateTask(task);
        assertEquals("description", this.taskManager.getTaskById(task.getId()).getDescription());
    }

    @Test
    public void updateEpic() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);
        epic.setName("new name");
        this.taskManager.updateEpic(epic);
        Assertions.assertEquals("new name", this.taskManager.getEpicById(epic.getId()).getName());
    }

    @Test
    public void updateSubTask() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);

        SubTask subTask = new SubTask("s", "s", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);

        subTask.setStatus(Status.DONE);
        this.taskManager.updateSubTask(subTask);

        assertEquals(Status.DONE, this.taskManager.getSubTaskById(subTask.getId()).getStatus());
    }

    @Test
    public void removeTask() {
        Task task = new Task("t", "t", Status.NEW);
        this.taskManager.addTask(task);
        assertEquals(1, this.taskManager.getTasksList().size());

        this.taskManager.removeTask(task.getId());
        assertEquals(0, this.taskManager.getTasksList().size());

        assertNull(this.taskManager.getTaskById(task.getId()));
    }

    @Test
    public void removeEpic() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);

        SubTask subTask = new SubTask("s", "s", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);

        assertEquals(epic, this.taskManager.getEpicById(epic.getId()));

        this.taskManager.removeEpic(epic.getId());
        assertNull(this.taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void removeSubTask() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);

        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);

        assertEquals(subTask, this.taskManager.getSubTaskById(subTask.getId()));

        this.taskManager.removeSubTask(subTask.getId());
        assertNull(this.taskManager.getSubTaskById(subTask.getId()));
    }

    @Test
    public void getEpicSubTasks() {
        Epic epic = new Epic("t", "t");
        this.taskManager.addEpic(epic);

        SubTask subTask = new SubTask("t", "t", Status.NEW, epic.getId());
        this.taskManager.addSubTask(subTask);

        assertEquals(List.of(subTask), this.taskManager.getEpicSubTasks(epic.getId()));
    }

    @Test
    public void getHistory() {
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

        assertEquals(List.of(epic, subTask, task), this.taskManager.getHistory());

        Task anotherTask = new Task("another task", "at", Status.IN_PROGRESS);
        this.taskManager.addTask(anotherTask);

        this.taskManager.getTaskById(anotherTask.getId());

        assertEquals(List.of(epic, subTask, task, anotherTask), this.taskManager.getHistory());

        this.taskManager.removeEpic(epic.getId());

        assertEquals(List.of(task, anotherTask), this.taskManager.getHistory());

        this.taskManager.removeTask(task.getId());
        assertEquals(List.of(anotherTask), this.taskManager.getHistory());
    }

    @Test
    public void getPrioritizedTasks() {
        Instant now = Instant.now();

        Task task = new Task("d", "d", Status.NEW, Duration.ofMinutes(5), now);
        this.taskManager.addTask(task);

        Epic epic = new Epic("d", "d");
        this.taskManager.addEpic(epic);

        SubTask subTask = new SubTask("d", "d", Status.NEW, epic.getId(), Duration.ofMinutes(5), now.plus(Duration.ofMinutes(5)));
        this.taskManager.addSubTask(subTask);

        // without duration and startTime
        Task task1 = new Task("d", "d", Status.NEW);
        this.taskManager.addTask(task1);

        assertEquals(List.of(task, subTask), this.taskManager.getPrioritizedTasks());

        Task updatedTask = new Task(task.getName(), task.getDescription(), Status.IN_PROGRESS, Duration.ofMinutes(5), now.plus(Duration.ofMinutes(10)));
        updatedTask.setId(task.getId());
        this.taskManager.updateTask(updatedTask);

        assertEquals(List.of(subTask, updatedTask), this.taskManager.getPrioritizedTasks());
    }
}
