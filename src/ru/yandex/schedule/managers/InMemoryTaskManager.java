package ru.yandex.schedule.managers;

import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.TaskType;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<String, Task> taskHashMap;

    private final HashMap<String, Epic> epicHashMap;

    private final HashMap<String, SubTask> subTaskHashMap;

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.taskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
        this.subTaskHashMap = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(taskHashMap.values());
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epicHashMap.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksList() {
        return new ArrayList<>(subTaskHashMap.values());
    }

    @Override
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

    @Override
    public Task getTaskById(String id) {
        Task task = taskHashMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(String id) {
        Epic epic = epicHashMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(String id) {
        SubTask subTask = subTaskHashMap.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void addTask(Task task) {
        taskHashMap.put(task.getId(), task);
    }

    @Override
    public void addTask(Epic epic) {
        epicHashMap.put(epic.getId(), epic);
    }

    @Override
    public void addTask(SubTask subTask) {
        subTaskHashMap.put(subTask.getId(), subTask);
    }

    @Override
    public void updateTask(Task task) {
        if (taskHashMap.containsKey(task.getId())) {
            taskHashMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic epicFound = epicHashMap.get(epic.getId());
        if (epicFound != null) {
            epicFound.name = epic.name;
            epicFound.description = epic.description;
            epicHashMap.put(epicFound.getId(), epicFound);
        }
    }

    @Override
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

    @Override
    public void removeTask(String id) {
        taskHashMap.remove(id);
    }

    @Override
    public void removeEpic(String id) {
        Epic epic = epicHashMap.get(id);
        if (epic != null) {
            epic.getSubTasks().forEach((s) -> subTaskHashMap.remove(s.getId()));
            epic.clearSubTasks();
            epicHashMap.remove(id);
        }
    }

    @Override
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

    @Override
    public ArrayList<SubTask> getEpicSubTasks(String epicId) {
        return epicHashMap.get(epicId).getSubTasks();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}
