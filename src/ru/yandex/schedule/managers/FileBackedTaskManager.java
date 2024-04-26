package ru.yandex.schedule.managers;

import ru.yandex.schedule.managers.exceptions.ManagerSaveException;
import ru.yandex.schedule.managers.interfaces.HistoryManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;
import ru.yandex.schedule.tasks.enums.TaskType;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.schedule.utils.TaskParser.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        File file1 = new File("src/resources/tasks.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file1);

        Instant now = Instant.now();

        Task task = new Task("name", "desc", Status.NEW, Duration.ofMinutes(5), now);
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic("e", "d");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "d", Status.IN_PROGRESS, epic.getId(), Duration.ofMinutes(10), now);
        fileBackedTaskManager.addSubTask(subTask);

        fileBackedTaskManager.getTaskById(task.getId());
        fileBackedTaskManager.getEpicById(epic.getId());
        fileBackedTaskManager.getSubTaskById(subTask.getId());
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeAllTaskByType(TaskType taskType) {
        super.removeAllTaskByType(taskType);
        save();
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            // if all the lists are empty write file with empty
            boolean isDataExist = false;
            List<Task> taskList = super.getTasksList();
            List<SubTask> subTaskList = super.getSubTasksList();
            List<Epic> epicList = super.getEpicsList();
            List<Task> history = super.getHistory();

            StringBuilder stringBuilder = new StringBuilder("id,type,name,status,description,duration,startTime,epic\n");

            if (!taskList.isEmpty()) {
                isDataExist = true;
                String tasksString = taskList
                        .stream()
                        .reduce("", (prev, task) -> prev + task.toString() + '\n', (s1, s2) -> s1 + s2);

                stringBuilder.append(tasksString);
            }

            if (!subTaskList.isEmpty()) {
                isDataExist = true;
                String subtasksString = subTaskList
                        .stream()
                        .reduce("", (prev, subtask) -> prev + subtask.toString() + '\n', (s1, s2) -> s1 + s2);

                stringBuilder.append(subtasksString);
            }

            if (!epicList.isEmpty()) {
                isDataExist = true;
                String epicsString = epicList
                        .stream()
                        .reduce("", (prev, epic) -> prev + epic.toString() + '\n', (s1, s2) -> s1 + s2);
                stringBuilder.append(epicsString);
            }

            stringBuilder.append('\n');

            if (!history.isEmpty()) {
                isDataExist = true;
                stringBuilder.append(historyToString(super.historyManager));
            }

            if (isDataExist) {
                bufferedWriter.write(stringBuilder.toString());
            } else {
                bufferedWriter.write("");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, historySize = history.size(); i < historySize; i++) {
            Task task = history.get(i);
            String result = Integer.toString(task.getId()) + (i < historySize - 1 ? ',' : "");
            stringBuilder.append(result);
        }
        return stringBuilder.toString();
    }

    public static List<Integer> getHistoryIdsFromString(String value) {
        String[] ids = value.split(",");
        try {
            return Arrays.stream(ids).map(Integer::parseInt).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Task fromString(String str) {
        try {
            String[] split = str.split(",");
            TaskType type = TaskType.valueOf(split[1].toUpperCase());
            switch (type) {
                case TASK:
                    return stringToTask(split);
                case EPIC: {
                    Epic epic = stringToEpic(split);
                    this.subTaskHashMap.forEach((id, subTask) -> {
                        if (subTask.getEpicId() == epic.getId()) {
                            epic.addSubTask(subTask);
                        }
                    });
                    return epic;
                }
                case SUBTASK:
                    return stringToSubtask(split);
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный id");
            e.printStackTrace();
        }
        return null;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        boolean allTasksFilled = false;
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            // skip first line
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                if (line.isEmpty() || line.isBlank()) {
                    // if caught empty line then left only history ids
                    allTasksFilled = true;
                    break;
                }

                String[] split = line.split(",");
                TaskType type = TaskType.valueOf(split[1].toUpperCase());
                Task task = fileBackedTaskManager.fromString(line);
                switch (type) {
                    case TASK:
                        fileBackedTaskManager.id = Math.max(task.getId(), fileBackedTaskManager.id);
                        fileBackedTaskManager.taskHashMap.put(task.getId(), task);
                        break;
                    case EPIC:
                        fileBackedTaskManager.id = Math.max(task.getId(), fileBackedTaskManager.id);
                        fileBackedTaskManager.epicHashMap.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        fileBackedTaskManager.id = Math.max(task.getId(), fileBackedTaskManager.id);
                        fileBackedTaskManager.subTaskHashMap.put(task.getId(), (SubTask) task);
                        break;
                }
            }

            if (allTasksFilled) {
                List<Integer> ids = getHistoryIdsFromString(bufferedReader.readLine());
                for (Integer id : ids) {
                    if (fileBackedTaskManager.taskHashMap.containsKey(id)) {
                        fileBackedTaskManager.historyManager.add(fileBackedTaskManager.taskHashMap.get(id));
                    } else if (fileBackedTaskManager.epicHashMap.containsKey(id)) {
                        fileBackedTaskManager.historyManager.add(fileBackedTaskManager.epicHashMap.get(id));
                    } else if (fileBackedTaskManager.subTaskHashMap.containsKey(id)) {
                        fileBackedTaskManager.historyManager.add(fileBackedTaskManager.subTaskHashMap.get(id));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBackedTaskManager;
    }
}
