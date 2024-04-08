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
    protected final HashMap<Integer, Task> taskHashMap;

    protected final HashMap<Integer, Epic> epicHashMap;

    protected final HashMap<Integer, SubTask> subTaskHashMap;

    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.taskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
        this.subTaskHashMap = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.id = 0;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(this.taskHashMap.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(this.epicHashMap.values());
    }

    @Override
    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(this.subTaskHashMap.values());
    }

    @Override
    public void removeAllTaskByType(TaskType taskType) {
        switch (taskType) {
            case TASK: {
                this.taskHashMap.forEach((id, task) -> this.historyManager.remove(id));
                this.taskHashMap.clear();
                break;
            }
            case EPIC: {
                this.epicHashMap.forEach((id, epic) -> this.historyManager.remove(id));
                this.subTaskHashMap.forEach((id, subTask) -> this.historyManager.remove(id));
                this.epicHashMap.clear();
                this.subTaskHashMap.clear();
                break;
            }
            case SUBTASK: {
                this.subTaskHashMap.forEach((id, subTask) -> this.historyManager.remove(id));
                this.subTaskHashMap.clear();
                break;
            }
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = this.taskHashMap.get(id);
        this.historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = this.epicHashMap.get(id);
        this.historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = this.subTaskHashMap.get(id);
        if (subTask != null) {
            this.historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public void addTask(Task task) {
        task.setId(++id);
        this.taskHashMap.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(++id);
        this.epicHashMap.put(epic.getId(), epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(++id);
        Epic epic = this.epicHashMap.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTask(subTask);
            this.subTaskHashMap.put(subTask.getId(), subTask);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (this.taskHashMap.containsKey(task.getId())) {
            this.taskHashMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic epicFound = this.epicHashMap.get(epic.getId());
        if (epicFound != null) {
            epicFound.setName(epic.getName());
            epicFound.setDescription(epicFound.getDescription());
            this.epicHashMap.put(epicFound.getId(), epicFound);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Epic epic = this.epicHashMap.get(subTask.getEpicId());
        if (epic != null) {
            boolean isUpdated = epic.updateSubTask(subTask);
            if (isUpdated) {
                if (this.subTaskHashMap.containsKey(subTask.getId())) {
                    this.subTaskHashMap.put(subTask.getId(), subTask);
                }
            }
        }
    }

    @Override
    public void removeTask(int id) {
        this.taskHashMap.remove(id);
        this.historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = this.epicHashMap.get(id);
        if (epic != null) {
            epic.getSubTasks().forEach(s -> {
                this.subTaskHashMap.remove(s.getId());
                this.historyManager.remove(s.getId());
            });
            epic.clearSubTasks();
            this.epicHashMap.remove(id);
            this.historyManager.remove(id);
        }
    }

    @Override
    public void removeSubTask(int id) {
        SubTask subTask = this.subTaskHashMap.get(id);
        if (subTask != null) {
            Epic epic = this.epicHashMap.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubTask(subTask);
            }
            this.subTaskHashMap.remove(id);
            this.historyManager.remove(id);
        }
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        return this.epicHashMap.get(epicId).getSubTasks();
    }

    @Override
    public List<Task> getHistory() {
        return this.historyManager.getHistory();
    }
}
