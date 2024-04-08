package ru.yandex.schedule.managers;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import ru.yandex.schedule.tasks.Epic;
import ru.yandex.schedule.tasks.SubTask;
import ru.yandex.schedule.tasks.Task;
import ru.yandex.schedule.tasks.enums.Status;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {

    private File file;

    @BeforeEach
    void beforeEach() {
        try {
            Path path = Files.createTempFile("tasks", ".csv");
            file = path.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void saveToEmptyFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("name", "desc", Status.NEW);
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic("e", "d");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "d", Status.IN_PROGRESS, epic.getId());
        fileBackedTaskManager.addSubTask(subTask);

        fileBackedTaskManager.getTaskById(task.getId());
        fileBackedTaskManager.getEpicById(epic.getId());
        fileBackedTaskManager.getSubTaskById(subTask.getId());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals("id,type,name,status,description,epic", lines.get(0));
            assertEquals("1,TASK,name,NEW,desc", lines.get(1));
            assertEquals("3,SUBTASK,s,IN_PROGRESS,d,2", lines.get(2));
            assertEquals("2,EPIC,e,IN_PROGRESS,d", lines.get(3));
            assertEquals("", lines.get(4));
            assertEquals("1,2,3", lines.get(5));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void loadFromFile_empty() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(0, fileBackedTaskManager.getTasksList().size());
        assertEquals(0, fileBackedTaskManager.getEpicsList().size());
        assertEquals(0, fileBackedTaskManager.getSubTasksList().size());
        assertEquals(0, fileBackedTaskManager.getHistory().size());
    }

    @Test
    void loadFromFile_filled() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,epic\n" +
                    "1,TASK,name,NEW,desc\n" +
                    "3,SUBTASK,s,IN_PROGRESS,d,2\n" +
                    "2,EPIC,e,IN_PROGRESS,d\n" +
                    "\n" +
                    "1,2,3");
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, fileBackedTaskManager.getTasksList().size());
        assertEquals(1, fileBackedTaskManager.getEpicsList().size());
        assertEquals(1, fileBackedTaskManager.getSubTasksList().size());
        assertEquals(3, fileBackedTaskManager.getHistory().size());
    }

    @Test
    void updateTasks() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("name", "desc", Status.NEW);
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic("e", "d");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "d", Status.IN_PROGRESS, epic.getId());
        fileBackedTaskManager.addSubTask(subTask);

        Task taskFromManager = fileBackedTaskManager.getTaskById(task.getId());
        Epic epicFromManager = fileBackedTaskManager.getEpicById(epic.getId());
        SubTask subTaskFromManager = fileBackedTaskManager.getSubTaskById(subTask.getId());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals("id,type,name,status,description,epic", lines.get(0));
            assertEquals("1,TASK,name,NEW,desc", lines.get(1));
            assertEquals("3,SUBTASK,s,IN_PROGRESS,d,2", lines.get(2));
            assertEquals("2,EPIC,e,IN_PROGRESS,d", lines.get(3));
            assertEquals("", lines.get(4));
            assertEquals("1,2,3", lines.get(5));
        } catch (IOException e) {
            e.printStackTrace();
        }

        taskFromManager.setName("Updated Name");
        fileBackedTaskManager.updateTask(task);
        epicFromManager.setName("Updated Name");
        fileBackedTaskManager.updateEpic(epicFromManager);
        subTaskFromManager.setName("Updated Name");
        fileBackedTaskManager.updateSubTask(subTaskFromManager);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals("id,type,name,status,description,epic", lines.get(0));
            assertEquals("1,TASK,Updated Name,NEW,desc", lines.get(1));
            assertEquals("3,SUBTASK,Updated Name,IN_PROGRESS,d,2", lines.get(2));
            assertEquals("2,EPIC,Updated Name,IN_PROGRESS,d", lines.get(3));
            assertEquals("", lines.get(4));
            assertEquals("1,2,3", lines.get(5));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeTasks() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("name", "desc", Status.NEW);
        fileBackedTaskManager.addTask(task);

        Epic epic = new Epic("e", "d");
        fileBackedTaskManager.addEpic(epic);
        SubTask subTask = new SubTask("s", "d", Status.IN_PROGRESS, epic.getId());
        fileBackedTaskManager.addSubTask(subTask);

        fileBackedTaskManager.getTaskById(task.getId());
        fileBackedTaskManager.getEpicById(epic.getId());
        fileBackedTaskManager.getSubTaskById(subTask.getId());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals("id,type,name,status,description,epic", lines.get(0));
            assertEquals("1,TASK,name,NEW,desc", lines.get(1));
            assertEquals("3,SUBTASK,s,IN_PROGRESS,d,2", lines.get(2));
            assertEquals("2,EPIC,e,IN_PROGRESS,d", lines.get(3));
            assertEquals("", lines.get(4));
            assertEquals("1,2,3", lines.get(5));
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileBackedTaskManager.removeTask(task.getId());
        fileBackedTaskManager.removeEpic(epic.getId());

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            assertEquals(0, lines.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    void getHistoryIdsFromString() {
        assertEquals(List.of(1, 2, 3), FileBackedTaskManager.getHistoryIdsFromString("1,2,3"));
        FileBackedTaskManager.getHistoryIdsFromString("1,2,d");
        exception.expect(NumberFormatException.class);
    }
}
