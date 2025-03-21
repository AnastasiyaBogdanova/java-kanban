package task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

class SubtaskTest {
    @Test
    void subTasksEquals() {
        Subtask subTask1 = new Subtask(1,
                "Покормить кота",
                "кормом",
                Status.NEW,
                1,
                Duration.ofMinutes(10),
                LocalDateTime.now());
        Subtask subTask2 = new Subtask(1,
                "Покормить собаку",
                "мясом",
                Status.IN_PROGRESS,
                2,
                Duration.ofMinutes(10),
                LocalDateTime.now());
        Assertions.assertEquals(subTask1, subTask2, "Не равны по Id");
    }
}