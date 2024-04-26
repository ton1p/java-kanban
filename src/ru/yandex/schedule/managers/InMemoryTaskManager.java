package ru.yandex.schedule.managers;

import ru.yandex.schedule.managers.exceptions.OverlapException;
import ru.yandex.schedule.managers.interfaces.HistoryManager;
import ru.yandex.schedule.managers.interfaces.TaskManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.TaskType;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id;
    protected final HashMap<Integer, Task> taskHashMap;

    protected final HashMap<Integer, Epic> epicHashMap;

    protected final HashMap<Integer, SubTask> subTaskHashMap;

    protected final HistoryManager historyManager;

    private final TreeSet<Task> sortedTasks;

    public InMemoryTaskManager() {
        this.taskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
        this.subTaskHashMap = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.id = 0;
        this.sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    private void addToSorted(Task task) throws OverlapException {
        boolean isTaskOverlap = this.sortedTasks.stream().anyMatch(t -> isTasksOverlap(task, t));

        if (isTaskOverlap) {
            throw new OverlapException("Задача пересекается по времени.");
        }

        List<Task> existsTasks = this.getPrioritizedTasks()
                .stream()
                .filter(t -> t.getId() == task.getId())
                .collect(Collectors.toList());

        if (!existsTasks.isEmpty()) {
            this.sortedTasks.remove(existsTasks.get(0));
            this.sortedTasks.add(task);
        } else {
            this.sortedTasks.add(task);
        }
    }

    private void removeFromSortedById(int id) {
        Task task = this.taskHashMap.getOrDefault(
                id,
                this.subTaskHashMap.getOrDefault(id, null)
        );

        if (task != null) {
            this.sortedTasks.remove(task);
        }
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
                this.sortedTasks.clear();
                break;
            }
            case EPIC: {
                this.epicHashMap.forEach((id, epic) -> this.historyManager.remove(id));
                this.subTaskHashMap.forEach((id, subTask) -> this.historyManager.remove(id));
                this.epicHashMap.clear();
                this.subTaskHashMap.clear();
                this.sortedTasks.clear();
                break;
            }
            case SUBTASK: {
                this.subTaskHashMap.forEach((id, subTask) -> this.historyManager.remove(id));
                this.subTaskHashMap.clear();
                this.sortedTasks.clear();
                break;
            }
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = this.taskHashMap.get(id);
        if (task != null) {
            this.historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = this.epicHashMap.get(id);
        if (epic != null) {
            this.historyManager.add(epic);
        }
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
        if (task != null) {
            task.setId(++id);
            if (task.getStartTime() != null && task.getDuration() != null) {
                try {
                    addToSorted(task);
                    this.taskHashMap.put(task.getId(), task);
                } catch (OverlapException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                this.taskHashMap.put(task.getId(), task);
            }
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic != null) {
            epic.setId(++id);
            this.epicHashMap.put(epic.getId(), epic);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask != null) {
            subTask.setId(++id);
            if (subTask.getStartTime() != null && subTask.getDuration() != null) {
                try {
                    Epic epic = this.epicHashMap.get(subTask.getEpicId());
                    if (epic != null) {
                        addToSorted(subTask);
                        epic.addSubTask(subTask);
                        this.subTaskHashMap.put(subTask.getId(), subTask);
                    }
                } catch (OverlapException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                Epic epic = this.epicHashMap.get(subTask.getEpicId());
                if (epic != null) {
                    epic.addSubTask(subTask);
                    this.subTaskHashMap.put(subTask.getId(), subTask);
                }
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (this.taskHashMap.containsKey(task.getId())) {
            if (task.getStartTime() != null && task.getDuration() != null) {
                try {
                    addToSorted(task);
                    this.taskHashMap.put(task.getId(), task);
                } catch (OverlapException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                this.taskHashMap.put(task.getId(), task);
            }
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
        if (epic != null && (this.subTaskHashMap.containsKey(subTask.getId()))) {
            if (subTask.getStartTime() != null && subTask.getDuration() != null) {
                try {
                    addToSorted(subTask);
                    epic.updateSubTask(subTask);
                    this.subTaskHashMap.put(subTask.getId(), subTask);
                } catch (OverlapException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                epic.updateSubTask(subTask);
                this.subTaskHashMap.put(subTask.getId(), subTask);
            }

        }
    }

    @Override
    public void removeTask(int id) {
        this.taskHashMap.remove(id);
        this.historyManager.remove(id);
        removeFromSortedById(id);
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
            removeFromSortedById(id);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return this.sortedTasks
                .stream()
                .filter(t -> t.getDuration() != null && t.getStartTime() != null)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isTasksOverlap(Task a, Task b) {
        long aStart = a.getStartTime().toEpochMilli();
        long aEnd = a.getEndTime().toEpochMilli();

        long bStart = b.getStartTime().toEpochMilli();
        long bEnd = b.getEndTime().toEpochMilli();

        if (aStart == bStart && aEnd == bEnd) {
            return true;
        }

        if (aStart == bStart || aEnd == bEnd) {
            return true;
        }

        if (aStart >= bEnd) {
            return false;
        }

        return aEnd > bStart;
    }
}
