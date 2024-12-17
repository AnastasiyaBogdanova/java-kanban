import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasksEquals() {
        Task task1 = new Task(1, "Покормить кота", "кормом", Status.NEW);
        Task task2 = new Task(1, "Покормить собаку", "мясом", Status.IN_PROGRESS);
        Assertions.assertEquals(task1, task2, "Не равны по Id");
    }

}