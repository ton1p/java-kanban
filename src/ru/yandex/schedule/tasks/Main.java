package ru.yandex.schedule.tasks;


import ru.yandex.schedule.manager.TaskManager;

public class Main {
    public static TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        Task task = new Task("Пнуть скорлупу за вафлерство", "А ведь так хорошо пришился", Status.NEW);
        Task task1 = new Task("Сходить в подвал", "Перетереть за скорлупу, и что они себе позволяют", Status.NEW);

        Epic epic = new Epic("Отжать район у борзых", "Утюги попутали берега");
        Epic epic1 = new Epic("Сходка", "Раздать лещей по возрастам");

        SubTask subTask = new SubTask("Забрать утюги", "Ночью завалиться к ним на район и втихую забрать все утюги", Status.NEW, epic.getId());
        SubTask subTask1 = new SubTask("На утро придти и поставить на счетчик", "Без утюгов им нечего нам противопоставить", Status.NEW, epic.getId());
        SubTask subTask2 = new SubTask("Колотун уже совсем взрослый", "Нужно дать ему первому зарядить скорлупе", Status.NEW, epic.getId());

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

        printData();

        task.status = Status.IN_PROGRESS;
        task1.description = "Обновил описание";
        taskManager.updateTask(task);
        taskManager.updateTask(task1);

        taskManager.updateEpic(epic.getId(), "Обновил название эпика", epic.description);

        subTask.status = Status.IN_PROGRESS;
        taskManager.updateSubTask(subTask);

        printData();

        subTask.status = Status.DONE;
        subTask1.status = Status.DONE;
        taskManager.updateSubTask(subTask);
        taskManager.updateSubTask(subTask1);

        printData();

        taskManager.removeTask(task.getId());
        taskManager.removeEpic(epic.getId());

        printData();
    }

    public static void printData() {
        System.out.println("---------------------------------");
        taskManager.getTasksList().forEach(System.out::println);
        System.out.println("---------------------------------");

        taskManager.getEpicsList().forEach(System.out::println);
        System.out.println("---------------------------------");

        taskManager.getSubTasksList().forEach(System.out::println);
        System.out.println("---------------------------------");
    }
}
