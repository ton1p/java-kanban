package ru.yandex.schedule.managers.interfaces;

import ru.yandex.schedule.managers.exceptions.NotFoundException;
import ru.yandex.schedule.managers.exceptions.OverlapException;
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

    Task getTaskById(int id) throws NotFoundException;

    Epic getEpicById(int id) throws NotFoundException;

    SubTask getSubTaskById(int id) throws NotFoundException;

    void addTask(Task task) throws OverlapException;

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask) throws OverlapException, NotFoundException;

    void updateTask(Task task) throws OverlapException, NotFoundException;

    void updateEpic(Epic epic) throws NotFoundException;

    void updateSubTask(SubTask subTask) throws OverlapException, NotFoundException;

    void removeTask(int id) throws NotFoundException;

    void removeEpic(int id) throws NotFoundException;

    void removeSubTask(int id) throws NotFoundException;

    List<SubTask> getEpicSubTasks(int epicId) throws NotFoundException;

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
