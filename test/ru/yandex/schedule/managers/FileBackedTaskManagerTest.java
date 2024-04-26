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
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskMangerTest<FileBackedTaskManager> {
    private File file;

    private final String startLine = "id,type,name,status,description,duration,startTime,epic";

    @BeforeEach
    void beforeEach() {
        createTaskManager();
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            Path path = Files.createTempFile("tasks", ".csv");
            this.file = path.toFile();
            return new FileBackedTaskManager(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

            assertEquals(startLine, lines.get(0));
            assertEquals("1,TASK,name,NEW,desc,null,null", lines.get(1));
            assertEquals("3,SUBTASK,s,IN_PROGRESS,d,null,null,2", lines.get(2));
            assertEquals("2,EPIC,e,IN_PROGRESS,d,null,null", lines.get(3));
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
            bufferedWriter.write("id,type,name,status,description,duration,startTime,epic\n" +
                    "1,TASK,name,NEW,desc,null,null\n" +
                    "3,SUBTASK,s,IN_PROGRESS,d,null,null,2\n" +
                    "2,EPIC,e,IN_PROGRESS,d,null,null\n" +
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
        assertEquals(3, fileBackedTaskManager.id);
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

            assertEquals(startLine, lines.get(0));
            assertEquals("1,TASK,name,NEW,desc,null,null", lines.get(1));
            assertEquals("3,SUBTASK,s,IN_PROGRESS,d,null,null,2", lines.get(2));
            assertEquals("2,EPIC,e,IN_PROGRESS,d,null,null", lines.get(3));
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

            assertEquals(startLine, lines.get(0));
            assertEquals("1,TASK,Updated Name,NEW,desc,null,null", lines.get(1));
            assertEquals("3,SUBTASK,Updated Name,IN_PROGRESS,d,null,null,2", lines.get(2));
            assertEquals("2,EPIC,Updated Name,IN_PROGRESS,d,null,null", lines.get(3));
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

            assertEquals(startLine, lines.get(0));
            assertEquals("1,TASK,name,NEW,desc,null,null", lines.get(1));
            assertEquals("3,SUBTASK,s,IN_PROGRESS,d,null,null,2", lines.get(2));
            assertEquals("2,EPIC,e,IN_PROGRESS,d,null,null", lines.get(3));
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

    @Test
    void fromString() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        // subtask test
        String subTaskString = "4,SUBTASK,s2,NEW,s2,null,null,1";
        SubTask expectedSubTask = new SubTask("s2", "s2", Status.NEW, 1);
        expectedSubTask.setId(4);

        assertEquals(expectedSubTask, fileBackedTaskManager.fromString(subTaskString));

        String subTaskStringWithDurationAndStartTime = "4,SUBTASK,s2,NEW,s2,5,2024-04-22T13:47:23.074683Z,1";
        expectedSubTask = new SubTask("s2", "s2", Status.NEW, 1, Duration.ofMinutes(5), Instant.parse("2024-04-22T13:47:23.074683Z"));
        expectedSubTask.setId(4);

        assertEquals(expectedSubTask, fileBackedTaskManager.fromString(subTaskStringWithDurationAndStartTime));

        // task test
        String taskString = "1,TASK,name,NEW,desc,null,null";
        Task expectedTask = new Task("name", "desc", Status.NEW);
        expectedTask.setId(1);

        assertEquals(expectedTask, fileBackedTaskManager.fromString(taskString));

        String taskStringWithDurationAndStartTime = "1,TASK,name,NEW,desc,5,2024-04-22T13:47:23.074683Z";
        expectedTask = new Task("name", "desc", Status.NEW, Duration.ofMinutes(5), Instant.parse("2024-04-22T13:47:23.074683Z"));
        expectedTask.setId(1);

        assertEquals(expectedTask, fileBackedTaskManager.fromString(taskStringWithDurationAndStartTime));

        // epic test
        String epicString = "1,EPIC,name,NEW,desc,null,null";
        Epic expectedEpic = new Epic("name", "desc");
        expectedEpic.setId(1);

        assertEquals(expectedEpic, fileBackedTaskManager.fromString(epicString));
    }
}
