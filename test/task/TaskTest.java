package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {

    @Test
    void tasksEquals() {
        Task task1 = new Task(1,
                "Покормить кота",
                "кормом",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.now());
        Task task2 = new Task(1,
                "Покормить собаку",
                "мясом",
                Status.IN_PROGRESS,
                Duration.ofMinutes(10),
                LocalDateTime.now());
        Assertions.assertEquals(task1, task2, "Не равны по Id");
    }

}