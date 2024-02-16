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
        ArrayList<Task> tasks = new ArrayList<>();
        taskHashMap.forEach((k, v) -> tasks.add(v));
        return tasks;
    }

    public ArrayList<Epic> getEpicsList() {
        ArrayList<Epic> epics = new ArrayList<>();
        epicHashMap.forEach((k, v) -> epics.add(v));
        return epics;
    }

    public ArrayList<SubTask> getSubTasksList() {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        subTaskHashMap.forEach((k, v) -> subTasks.add(v));
        return subTasks;
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
        if (taskHashMap.containsKey(id)) {
            return taskHashMap.get(id);
        }

        return null;
    }

    public Epic getEpicById(String id) {
        if (epicHashMap.containsKey(id)) {
            return epicHashMap.get(id);
        }

        return null;
    }

    public SubTask getSubTaskById(String id) {
        if (subTaskHashMap.containsKey(id)) {
            return subTaskHashMap.get(id);
        }

        return null;
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

    public void updateTask(Epic epic) {
        if (epicHashMap.containsKey(epic.getId())) {
            epicHashMap.put(epic.getId(), epic);
        }
    }

    public void updateTask(SubTask subTask) {
        if (subTaskHashMap.containsKey(subTask.getId())) {
            SubTask old = subTaskHashMap.get(subTask.getId());
            Epic epic = epicHashMap.get(subTask.epicId);
            ArrayList<SubTask> subTasks = new ArrayList<>();
            for (SubTask task : epic.getSubTasks()) {
                subTasks.add(task);
            }
            subTasks.remove(old);
            subTasks.add(subTask);
            epic.setSubTasks(subTasks);

            subTaskHashMap.put(subTask.getId(), subTask);
        }
    }

    public void removeTaskById(String id) {
        if (taskHashMap.containsKey(id)) {
            taskHashMap.remove(id);
            return;
        }

        if (epicHashMap.containsKey(id)) {
            Epic epic = epicHashMap.get(id);
            for (SubTask subTask : epic.getSubTasks()) {
                subTaskHashMap.remove(subTask.getId());
            }
            epicHashMap.remove(id);
            return;
        }

        if (subTaskHashMap.containsKey(id)) {
            SubTask subTask = subTaskHashMap.get(id);
            Epic epic = epicHashMap.get(subTask.epicId);
            ArrayList<SubTask> subTasks = new ArrayList<>();
            for (SubTask task : epic.getSubTasks()) {
                subTasks.add(task);
            }
            subTasks.remove(subTask);
            epic.setSubTasks(subTasks);

            subTaskHashMap.remove(id);
        }
    }

    public ArrayList<SubTask> getSubTasks(String epicId) {
        if (epicHashMap.containsKey(epicId)) {
            return epicHashMap.get(epicId).getSubTasks();
        }
        return null;
    }
}
