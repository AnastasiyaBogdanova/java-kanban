import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Subtask;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void subTasksEquals() {
        Subtask subTask1 = new Subtask(1, "Покормить кота", "кормом", Status.NEW, 1);
        Subtask subTask2 = new Subtask(1, "Покормить собаку", "мясом", Status.IN_PROGRESS, 2);
        Assertions.assertEquals(subTask1, subTask2, "Не равны по Id");
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getEpicId() {
    }

    @Test
    void testToString() {
    }
}