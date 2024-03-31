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

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubTask(int id);

    List<SubTask> getEpicSubTasks(int epicId);

    List<Task> getHistory();
}
