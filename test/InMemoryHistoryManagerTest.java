import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void createHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void getHistory() {
        Task task = new Task(1, "Покормить кота", "кормом", Status.NEW);
        historyManager.add(task);
        Task taskExpected = new Task(1, "Покормить кота", "кормом", Status.NEW);
        final List<Task> history = historyManager.getHistory();
        assertEquals(taskExpected, history.get(0), "Не равны");
    }

    @Test
    void addObjectInHistory() {
        Task task = new Task(1, "Покормить кота", "кормом", Status.NEW);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addTenPlusOneObjectInHistory() {
        // при добавлении 11 элемента должен удалиться
        Task task1 = new Task(1, "Покормить кота", "кормом", Status.NEW);
        Task taskExpected = new Task(2, "Покормить собаку", "кормом", Status.NEW);

        Task task2 = new Task(2, "Покормить собаку", "кормом", Status.NEW);
        Task task3 = new Task(3, "Покормить хомяка", "кормом", Status.NEW);
        Task task4 = new Task(4, "Покормить кур", "кормом", Status.NEW);
        Task task5 = new Task(5, "Покормить свинью", "кормом", Status.NEW);
        Task task6 = new Task(6, "Покормить кролика", "кормом", Status.NEW);
        Task task7 = new Task(7, "Покормить пауков", "кормом", Status.NEW);
        Task task8 = new Task(8, "Покормить крыс", "кормом", Status.NEW);
        Task task9 = new Task(9, "Покормить черепах", "кормом", Status.NEW);
        Task task10 = new Task(10, "Покормить мужа", "кормом", Status.NEW);
        Task task11 = new Task(11, "Покормить суслика", "кормом", Status.NEW);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(task7);
        historyManager.add(task8);
        historyManager.add(task9);
        historyManager.add(task10);
        historyManager.add(task11);


        final List<Task> history = historyManager.getHistory();
        assertEquals(taskExpected, history.get(0), "Не равны");
    }

    @Test
    void versionHistoryManager() {
        Task task = new Task(1, "Покормить кота", "кормом", Status.NEW);
        historyManager.add(task);
        TaskManager manager = Managers.getDefault();
        manager.addNewTask(task);
        manager.getTaskById(task.getId());
        task.setName("Убрать лоток");
        manager.getTaskById(task.getId());
        assertNotEquals(manager.getHistory().get(0).getName(), manager.getHistory().get(1).getName());
        assertEquals("Покормить кота", manager.getHistory().get(0).getName());
        assertEquals("Убрать лоток", manager.getHistory().get(1).getName());
    }

}