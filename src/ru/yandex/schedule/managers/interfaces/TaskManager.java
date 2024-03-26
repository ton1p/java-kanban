package ru.yandex.schedule.managers.interfaces;

import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.TaskType;

import java.util.List;

public interface TaskManager {
    List<Task> getTasksList();

    List<Epic> getEpicsList();

    List<SubTask> getSubTasksList();

    void removeAllTaskByType(TaskType taskType);

    Task getTaskById(String id);

    Epic getEpicById(String id);

    SubTask getSubTaskById(String id);

    void addTask(Task task);

    void addTask(Epic epic);

    void addTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeTask(String id);

    void removeEpic(String id);

    void removeSubTask(String id);

    List<SubTask> getEpicSubTasks(String epicId);

    List<Task> getHistory();
}
