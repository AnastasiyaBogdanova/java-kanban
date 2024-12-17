import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void epicsEquals() {
        Epic epic1 = new Epic(1, "Покормить кота", "кормом");
        Epic epic2 = new Epic(1, "Покормить собаку", "мясом");
        Assertions.assertEquals(epic1, epic2, "Не равны по Id");

    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getSubTasksId() {
    }

    @Test
    void removeSubTask() {
    }

    @Test
    void setSubTasksId() {
    }

    @Test
    void testToString() {
    }
}