package ru.yandex.schedule.manager;

import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.TaskType;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<String, Task> taskHashMap;

    private final HashMap<String, Epic> epicHashMap;

    private final HashMap<String, SubTask> subTaskHashMap;

    public TaskManager() {
        this.taskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
        this.subTaskHashMap = new HashMap<>();
    }

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(taskHashMap.values());
    }

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epicHashMap.values());
    }

    public ArrayList<SubTask> getSubTasksList() {
        return new ArrayList<>(subTaskHashMap.values());
    }

    public void removeAllTaskByType(TaskType taskType) {
        switch (taskType) {
            case TASK: {
                taskHashMap.clear();
                break;
            }
            case EPIC: {
                epicHashMap.clear();
                subTaskHashMap.clear();
                break;
            }
            case SUBTASK: {
                subTaskHashMap.clear();
                break;
            }
        }
    }

    public Task getTaskById(String id) {
        return taskHashMap.get(id);
    }

    public Epic getEpicById(String id) {
        return epicHashMap.get(id);
    }

    public SubTask getSubTaskById(String id) {
        return subTaskHashMap.get(id);
    }

    public void addTask(Task task) {
        taskHashMap.put(task.getId(), task);
    }

    public void addTask(Epic epic) {
        epicHashMap.put(epic.getId(), epic);
    }

    public void addTask(SubTask subTask) {
        subTaskHashMap.put(subTask.getId(), subTask);
    }

    public void updateTask(Task task) {
        if (taskHashMap.containsKey(task.getId())) {
            taskHashMap.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        Epic epicFound = epicHashMap.get(epic.getId());
        if (epicFound != null) {
            epicFound.name = epic.name;
            epicFound.description = epic.description;
            epicHashMap.put(epicFound.getId(), epicFound);
        }
    }

    public void updateSubTask(SubTask subTask) {
        Epic epic = epicHashMap.get(subTask.epicId);
        if (epic != null) {
            boolean isUpdated = epic.updateSubTask(subTask);
            if (isUpdated) {
                if (subTaskHashMap.containsKey(subTask.getId())) {
                    subTaskHashMap.put(subTask.getId(), subTask);
                }
            }
        }
    }

    public void removeTask(String id) {
        taskHashMap.remove(id);
    }

    public void removeEpic(String id) {
        Epic epic = epicHashMap.get(id);
        if (epic != null) {
            epic.getSubTasks().forEach((s) -> subTaskHashMap.remove(s.getId()));
            epic.clearSubTasks();
            epicHashMap.remove(id);
        }
    }

    public void removeSubTask(String id) {
        SubTask subTask = subTaskHashMap.get(id);
        if (subTask != null) {
            Epic epic = epicHashMap.get(subTask.epicId);
            if (epic != null) {
                epic.removeSubTask(subTask);
            }
            subTaskHashMap.remove(id);
        }
    }

    public ArrayList<SubTask> getEpicSubTasks(String epicId) {
        return epicHashMap.get(epicId).getSubTasks();
    }
}
