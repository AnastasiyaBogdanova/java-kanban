import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Subtask;

class SubtaskTest {
    @Test
    void subTasksEquals() {
        Subtask subTask1 = new Subtask(1, "Покормить кота", "кормом", Status.NEW, 1);
        Subtask subTask2 = new Subtask(1, "Покормить собаку", "мясом", Status.IN_PROGRESS, 2);
        Assertions.assertEquals(subTask1, subTask2, "Не равны по Id");
    }
}