package ru.yandex.schedule;

import ru.yandex.schedule.managers.Managers;
import ru.yandex.schedule.managers.TaskManager;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.Status;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;

public class Main {
    public static TaskManager taskManager = Managers.getDefaultTask();

    public static void main(String[] args) {
        Task task = new Task("Пнуть скорлупу за вафлерство", "А ведь так хорошо пришился", Status.NEW);
        Task task1 = new Task("Сходить в подвал", "Перетереть за скорлупу, и что они себе позволяют", Status.NEW);

        Epic epic = new Epic("Отжать район у борзых", "Утюги попутали берега");
        Epic epic1 = new Epic("Сходка", "Раздать лещей по возрастам");

        SubTask subTask = new SubTask("Забрать утюги", "Ночью завалиться к ним на район и втихую забрать все утюги", Status.NEW, epic.getId());
        SubTask subTask1 = new SubTask("На утро придти и поставить на счетчик", "Без утюгов им нечего нам противопоставить", Status.NEW, epic.getId());
        SubTask subTask2 = new SubTask("Колотун уже совсем взрослый", "Нужно дать ему первому зарядить скорлупе", Status.NEW, epic1.getId());

        epic.addSubTask(subTask);
        epic.addSubTask(subTask1);
        epic1.addSubTask(subTask2);

        taskManager.addTask(task);
        taskManager.addTask(task1);
        taskManager.addTask(epic);
        taskManager.addTask(epic1);
        taskManager.addTask(subTask);
        taskManager.addTask(subTask1);
        taskManager.addTask(subTask2);

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubTaskById(subTask.getId());
        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic.getId());

        printAllTasks(taskManager);

        task.setStatus(Status.IN_PROGRESS);
        task1.setDescription("Обновил описание");
        taskManager.updateTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.updateTask(task1);
        taskManager.getTaskById(task1.getId());

        printAllTasks(taskManager);

        epic.setName("Обновил название эпика");
        epic.setDescription("Обновил описание эпика");
        taskManager.updateEpic(epic);
        taskManager.getEpicById(epic.getId());

        printAllTasks(taskManager);

        subTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask);
        taskManager.getSubTaskById(subTask.getId());

        printAllTasks(taskManager);

        subTask.setStatus(Status.DONE);
        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        taskManager.getSubTaskById(subTask.getId());
        taskManager.updateSubTask(subTask1);
        taskManager.getSubTaskById(subTask1.getId());

        printAllTasks(taskManager);

        taskManager.removeTask(task.getId());
        taskManager.removeEpic(epic.getId());

        printAllTasks(taskManager);

        taskManager.removeSubTask(subTask2.getId());

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
