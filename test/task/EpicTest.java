package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;

class EpicTest {
    @Test
    void epicsEquals() {
        Epic epic1 = new Epic(1, "Покормить кота", "кормом");
        Epic epic2 = new Epic(1, "Покормить собаку", "мясом");
        Assertions.assertEquals(epic1, epic2, "Не равны по Id");

    }
}