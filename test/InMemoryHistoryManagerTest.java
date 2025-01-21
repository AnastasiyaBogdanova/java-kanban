import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
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
    void addEqualsObjectInHistory() {
        // при добавлении такого же элемента более ранний должен удалиться
        Task task1 = new Task(1, "Покормить кота", "кормом", Status.NEW);
        Task task2 = new Task(2, "Покормить собаку", "кормом", Status.NEW);

        Task taskExpected1 = new Task(2, "Покормить собаку", "кормом", Status.NEW);
        Task taskExpected2 = new Task(1, "Покормить кота", "кормом", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        final List<Task> history = historyManager.getHistory();
        assertEquals(taskExpected1, history.get(0), "Не равны");
        assertEquals(taskExpected2, history.get(1), "Не равны");
    }

    @Test
    void versionHistoryManager() { //если изменить что-то в таске, просмотреть таск, то останется 1 таск с обновленными полями
        Task task = new Task(1, "Покормить кота", "кормом", Status.NEW);
        historyManager.add(task);
        TaskManager manager = Managers.getDefault();
        manager.addNewTask(task);
        manager.getTaskById(task.getId());
        task.setName("Убрать лоток");
        manager.getTaskById(task.getId());
        assertEquals("Убрать лоток", manager.getHistory().get(0).getName());
    }

    @Test
    void removeAllEpicsWithSubtasksFromHistory() {
        TaskManager manager = Managers.getDefault();

        Task task0 = new Task("Покормить кота", "кормом", Status.NEW);
        Epic epic1 = new Epic("Подготовка к НГ", "Список дел");
        manager.addNewTask(task0);
        manager.addNewEpic(epic1);

        Subtask subtask2 = new Subtask("Купить елку", "На рынке", Status.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Заказать игрушки", "На озоне или вб", Status.NEW, epic1.getId());
        manager.addNewSubTask(subtask2);
        manager.addNewSubTask(subtask3);

        manager.getTaskById(task0.getId());
        manager.getSubTaskById(subtask2.getId());
        manager.getEpicById(epic1.getId());

        assertEquals(3, manager.getHistory().size(), "Не все добавлено в историю");

        manager.removeEpicById(epic1.getId());

        assertEquals(1, manager.getHistory().size(), "Сабтаск не удален");

        assertEquals(false, manager.getHistory().contains(subtask2), "Сабтаск не удален");
    }

}