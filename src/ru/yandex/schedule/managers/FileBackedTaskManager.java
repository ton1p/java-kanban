package ru.yandex.schedule.managers;

import ru.yandex.schedule.managers.exceptions.ManagerSaveException;
import ru.yandex.schedule.managers.interfaces.HistoryManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;
import ru.yandex.schedule.tasks.enums.TaskType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.io.FileReader;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        File file1 = new File("src/resources/tasks.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file1);

        Task task = new Task("name", "desc", Status.NEW);
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic("e", "d");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "d", Status.IN_PROGRESS, epic.getId());
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

            StringBuilder stringBuilder = new StringBuilder("id,type,name,status,description,epic\n");

            if (!taskList.isEmpty()) {
                isDataExist = true;
                for (Task task : taskList) {
                    stringBuilder.append(task.toString()).append('\n');
                }
            }

            if (!subTaskList.isEmpty()) {
                isDataExist = true;
                for (SubTask subTask : subTaskList) {
                    stringBuilder.append(subTask.toString()).append('\n');
                }
            }

            if (!epicList.isEmpty()) {
                isDataExist = true;
                for (Epic epic : epicList) {
                    stringBuilder.append(epic.toString()).append('\n');
                }
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
        List<Integer> list = new ArrayList<>();
        try {
            for (String id : ids) {
                int toNumber = Integer.parseInt(id);
                list.add(toNumber);
            }
            return list;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Task fromString(String str) {
        try {
            String[] split = str.split(",");
            int id = Integer.parseInt(split[0]);
            TaskType type = TaskType.valueOf(split[1].toUpperCase());
            String name = split[2];
            Status status = Status.valueOf(split[3].toUpperCase());
            String description = split[4];

            int epicId = 0;
            if (split.length == 6 && (split[5] != null)) {
                epicId = Integer.parseInt(split[5]);
            }

            switch (type) {
                case TASK:
                    Task task = new Task(name, description, status);
                    task.setId(id);
                    return task;
                case EPIC:
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    return epic;
                case SUBTASK:
                    SubTask subTask = new SubTask(name, description, status, epicId);
                    subTask.setId(id);
                    return subTask;
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
