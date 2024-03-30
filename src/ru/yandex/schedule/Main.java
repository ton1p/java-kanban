package ru.yandex.schedule;

import ru.yandex.schedule.managers.Managers;
import ru.yandex.schedule.managers.interfaces.TaskManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;

public class Main {
    public static TaskManager taskManager = Managers.getDefaultTask();

    public static void main(String[] args) {
        Epic epic = new Epic("e1", "e1");
        taskManager.addTask(epic);
        for (int i = 0; i < 3; i++) {
            SubTask subTask = new SubTask("s" + i, "s" + i, Status.NEW, epic.getId());
            taskManager.addTask(subTask);
            epic.addSubTask(subTask);
        }
        Epic epic2 = new Epic("e2", "e2");
        taskManager.addTask(epic2);


        taskManager.getEpicById(epic2.getId());
        printAllTasks(taskManager);

        taskManager.getEpicById(epic2.getId());
        printAllTasks(taskManager);

        taskManager.getSubTaskById(epic.getSubTasks().get(1).getId());
        printAllTasks(taskManager);

        taskManager.getSubTaskById(epic.getSubTasks().get(0).getId());
        printAllTasks(taskManager);

        taskManager.getEpicById(epic.getId());
        printAllTasks(taskManager);

        taskManager.getEpicById(epic.getId());
        printAllTasks(taskManager);

        taskManager.getSubTaskById(epic.getSubTasks().get(2).getId());
        printAllTasks(taskManager);

        taskManager.removeSubTask(epic.getSubTasks().get(0).getId());
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasksList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpicsList()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubTasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasksList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
