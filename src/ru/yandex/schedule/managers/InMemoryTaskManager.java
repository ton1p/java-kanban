package ru.yandex.schedule.managers;

import ru.yandex.schedule.managers.exceptions.NotFoundException;
import ru.yandex.schedule.managers.exceptions.OverlapException;
import ru.yandex.schedule.managers.interfaces.HistoryManager;
import ru.yandex.schedule.managers.interfaces.TaskManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.TaskType;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

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
        Optional<Task> existsTask = this.sortedTasks
                .stream()
                .filter(t -> t.getId() == task.getId())
                .findFirst();

        if (existsTask.isPresent()) {
            this.sortedTasks.remove(existsTask.get());
            this.sortedTasks.add(task);
        } else {
            boolean isTaskOverlap = this.sortedTasks.stream().anyMatch(t -> isTasksOverlap(task, t));

            if (isTaskOverlap) {
                throw new OverlapException("Задача пересекается по времени.");
            }

            this.sortedTasks.add(task);
        }
    }

    private void removeFromSorted(Task task) {
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
                this.taskHashMap.forEach((taskId, task) -> {
                    this.historyManager.remove(taskId);
                    removeFromSorted(task);
                });
                this.taskHashMap.clear();
                break;
            }
            case EPIC: {
                this.epicHashMap.forEach((epicId, epic) -> this.historyManager.remove(epicId));
                this.subTaskHashMap.forEach((subTaskId, subTask) -> {
                    this.historyManager.remove(subTaskId);
                    removeFromSorted(subTask);
                });
                this.epicHashMap.clear();
                this.subTaskHashMap.clear();
                break;
            }
            case SUBTASK: {
                this.subTaskHashMap.forEach((subTaskId, subTask) -> {
                    this.historyManager.remove(subTaskId);
                    removeFromSorted(subTask);
                });
                this.subTaskHashMap.clear();
                break;
            }
        }
    }

    @Override
    public Task getTaskById(int id) throws NotFoundException {
        Task task = this.taskHashMap.get(id);
        if (task != null) {
            this.historyManager.add(task);
        } else {
            throw new NotFoundException(getNotFoundMessage(id, TaskType.TASK));
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) throws NotFoundException {
        Epic epic = this.epicHashMap.get(id);
        if (epic != null) {
            this.historyManager.add(epic);
        } else {
            throw new NotFoundException(getNotFoundMessage(id, TaskType.EPIC));
        }
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) throws NotFoundException {
        SubTask subTask = this.subTaskHashMap.get(id);
        if (subTask != null) {
            this.historyManager.add(subTask);
        } else {
            throw new NotFoundException(getNotFoundMessage(id, TaskType.SUBTASK));
        }
        return subTask;
    }

    @Override
    public void addTask(Task task) throws OverlapException {
        if (task != null) {
            task.setId(++id);
            if (task.getStartTime() != null && task.getDuration() != null) {
                try {
                    addToSorted(task);
                    this.taskHashMap.put(task.getId(), task);
                } catch (OverlapException e) {
                    this.id--;
                    throw new OverlapException(e.getMessage());
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
    public void addSubTask(SubTask subTask) throws OverlapException, NotFoundException {
        if (subTask != null) {
            subTask.setId(++id);
            int epicId = subTask.getEpicId();
            Epic epic = this.epicHashMap.get(epicId);
            if (epic != null) {
                if (subTask.getStartTime() != null && subTask.getDuration() != null) {
                    try {
                        addToSorted(subTask);
                        epic.addSubTask(subTask);
                        this.subTaskHashMap.put(subTask.getId(), subTask);
                    } catch (OverlapException e) {
                        this.id--;
                        throw new OverlapException(e.getMessage());
                    }
                } else {
                    epic.addSubTask(subTask);
                    this.subTaskHashMap.put(subTask.getId(), subTask);
                }
            } else {
                this.id--;
                throw new NotFoundException(getNotFoundMessage(epicId, TaskType.EPIC));
            }
        }
    }

    @Override
    public void updateTask(Task task) throws OverlapException, NotFoundException {
        if (this.taskHashMap.containsKey(task.getId())) {
            if (task.getStartTime() != null && task.getDuration() != null) {
                addToSorted(task);
                this.taskHashMap.put(task.getId(), task);
            } else {
                this.taskHashMap.put(task.getId(), task);
            }
        } else {
            throw new NotFoundException(getNotFoundMessage(task.getId(), TaskType.TASK));
        }
    }

    @Override
    public void updateEpic(Epic epic) throws NotFoundException {
        int epicId = epic.getId();
        Epic epicFound = this.epicHashMap.get(epicId);
        if (epicFound != null) {
            epicFound.setName(epic.getName());
            epicFound.setDescription(epicFound.getDescription());
            this.epicHashMap.put(epicId, epicFound);
        } else {
            throw new NotFoundException(getNotFoundMessage(epicId, TaskType.EPIC));
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) throws NotFoundException {
        int epicId = subTask.getEpicId();
        Epic epic = this.epicHashMap.get(epicId);
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

        } else {
            throw new NotFoundException(getNotFoundMessage(epicId, TaskType.EPIC));
        }
    }

    @Override
    public void removeTask(int id) throws NotFoundException {
        Task task = this.taskHashMap.remove(id);
        if (task == null) {
            throw new NotFoundException("Задача с id " + id + " не найдена");
        }
        this.historyManager.remove(id);
        removeFromSorted(task);
    }

    @Override
    public void removeEpic(int id) throws NotFoundException {
        Epic epic = this.epicHashMap.get(id);
        if (epic != null) {
            epic.getSubTasks().forEach(s -> {
                this.subTaskHashMap.remove(s.getId());
                removeFromSorted(s);
                this.historyManager.remove(s.getId());
            });
            epic.clearSubTasks();
            this.epicHashMap.remove(id);
            this.historyManager.remove(id);
        } else {
            throw new NotFoundException(getNotFoundMessage(id, TaskType.EPIC));
        }
    }

    @Override
    public void removeSubTask(int id) throws NotFoundException {
        SubTask subTask = this.subTaskHashMap.get(id);
        if (subTask != null) {
            int epicId = subTask.getEpicId();
            Epic epic = this.epicHashMap.get(epicId);
            if (epic != null) {
                epic.removeSubTask(subTask);
            } else {
                throw new NotFoundException(getNotFoundMessage(epicId, TaskType.SUBTASK));
            }
            this.subTaskHashMap.remove(id);
            this.historyManager.remove(id);
            removeFromSorted(subTask);
        } else {
            throw new NotFoundException(getNotFoundMessage(id, TaskType.SUBTASK));
        }
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) throws NotFoundException {
        Epic epic = this.epicHashMap.get(epicId);
        if (epic != null) {
            return epic.getSubTasks();
        } else {
            throw new NotFoundException(getNotFoundMessage(epicId, TaskType.EPIC));
        }
    }

    @Override
    public List<Task> getHistory() {
        return this.historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(this.sortedTasks);
    }

    public boolean isTasksOverlap(Task a, Task b) {
        Instant aStartTime = a.getStartTime();
        Instant aEndTime = a.getEndTime();

        Instant bStartTime = b.getStartTime();
        Instant bEndTime = b.getEndTime();

        if (aStartTime.equals(bStartTime) && aEndTime.equals(bEndTime)) {
            return true;
        }

        if (aStartTime.equals(bStartTime) || aEndTime.equals(bEndTime)) {
            return true;
        }

        if (aStartTime.isAfter(bEndTime) || aStartTime.equals(bEndTime)) {
            return false;
        }

        return aEndTime.isAfter(bStartTime);
    }

    private String getNotFoundMessage(int id, TaskType taskType) {
        String type = switch (taskType) {
            case TASK -> "Задача";
            case SUBTASK -> "Подзадача";
            case EPIC -> "Эпик";
        };

        String result = type + " с id " + id + " не найдена";
        return result.substring(0, taskType.equals(TaskType.EPIC) ? result.length() - 1 : result.length());
    }
}
