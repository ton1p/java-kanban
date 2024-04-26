package ru.yandex.schedule.managers;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.schedule.managers.interfaces.TaskManager;

class InMemoryTaskManagerTest extends TaskMangerTest<InMemoryTaskManager> {
    TaskManager taskManager;

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    void beforeEach() {
        this.taskManager = createTaskManager();
    }
}
