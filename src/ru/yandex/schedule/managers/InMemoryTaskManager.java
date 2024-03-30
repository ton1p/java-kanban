package ru.yandex.schedule.managers;

import ru.yandex.schedule.managers.interfaces.HistoryManager;
import ru.yandex.schedule.managers.interfaces.TaskManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private final HashMap<Integer, Task> taskHashMap;

    private final HashMap<Integer, Epic> epicHashMap;

    private final HashMap<Integer, SubTask> subTaskHashMap;

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.taskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
        this.subTaskHashMap = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.id = 0;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(taskHashMap.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epicHashMap.values());
    }

    @Override
    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subTaskHashMap.values());
    }

    @Override
    public void removeAllTaskByType(TaskType taskType) {
        switch (taskType) {
            case TASK: {
                taskHashMap.forEach((id, task) -> historyManager.remove(id));
                taskHashMap.clear();
                break;
            }
            case EPIC: {
                epicHashMap.forEach((id, epic) -> historyManager.remove(id));
                subTaskHashMap.forEach((id, subTask) -> historyManager.remove(id));
                epicHashMap.clear();
                subTaskHashMap.clear();
                break;
            }
            case SUBTASK: {
                subTaskHashMap.forEach((id, subTask) -> historyManager.remove(id));
                subTaskHashMap.clear();
                break;
            }
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskHashMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicHashMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTaskHashMap.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void addTask(Task task) {
        task.setId(++id);
        taskHashMap.put(task.getId(), task);
    }

    @Override
    public void addTask(Epic epic) {
        epic.setId(++id);
        epicHashMap.put(epic.getId(), epic);
        epic.getSubTasks().forEach(s -> subTaskHashMap.put(s.getId(), s));
    }

    @Override
    public void addTask(SubTask subTask) {
        subTask.setId(++id);
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
            epicFound.setName(epic.getName());
            epicFound.setDescription(epicFound.getDescription());
            epicHashMap.put(epicFound.getId(), epicFound);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Epic epic = epicHashMap.get(subTask.getEpicId());
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
    public void removeTask(int id) {
        taskHashMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epicHashMap.get(id);
        if (epic != null) {
            epic.getSubTasks().forEach((s) -> {
                subTaskHashMap.remove(s.getId());
                historyManager.remove(s.getId());
            });
            epic.clearSubTasks();
            epicHashMap.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubTask(int id) {
        SubTask subTask = subTaskHashMap.get(id);
        if (subTask != null) {
            Epic epic = epicHashMap.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubTask(subTask);
            }
            subTaskHashMap.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        return epicHashMap.get(epicId).getSubTasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
