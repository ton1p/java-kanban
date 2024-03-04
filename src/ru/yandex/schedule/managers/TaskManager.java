package ru.yandex.schedule.managers;

import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.TaskType;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getTasksList();

    ArrayList<Epic> getEpicsList();

    ArrayList<SubTask> getSubTasksList();

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

    ArrayList<SubTask> getEpicSubTasks(String epicId);

    ArrayList<Task> getHistory();
}
