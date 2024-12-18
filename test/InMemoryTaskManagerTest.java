import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import manager.*;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Epic epic1;
    private Task task;
    private static int epicId;
    private static int taskId;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        task = new Task("Купить хлеб", "важное дело", Status.NEW);
        epic1 = new Epic("Помыть кота", "с шампунем");
        epicId = taskManager.addNewEpic(epic1).getId();
        taskId = taskManager.addNewTask(task).getId();
    }

    @Test
    void addNewTaskAndReturnByIdAndCheckEquals() {
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "таск не найден.");
        assertEquals(task, savedTask, "таски не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "таски не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество тасков.");
        assertEquals(task, tasks.get(0), "таски не совпадают.");
    }

    @Test
    void addNewSubTaskAndReturnByIdAndCheckEquals() {
        Subtask subtask = new Subtask("нарезать салат", "из овощей", Status.NEW, epicId);
        final int subtaskId = taskManager.addNewSubTask(subtask).getId();

        final Subtask savedSubTask = taskManager.getSubTaskById(subtaskId);

        assertNotNull(savedSubTask, "сабтаск не найден.");
        assertEquals(subtask, savedSubTask, "сабтаски не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubTasks();

        assertNotNull(subtasks, "сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасков.");
        assertEquals(subtask, subtasks.get(0), "сабтаски не совпадают.");
    }

    @Test
    void addNewEpicAndReturnByIdAndCheckEquals() {
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "эпик не найден.");
        assertEquals(epic1, savedEpic, "эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.get(0), "эпики не совпадают.");
    }

    @Test
    void AllFieldsAddingTaskEqualsAddedTask() {
        String name = task.getName();
        String desc = task.getDescription();
        Status status = task.getStatus();
        assertEquals(taskId, taskManager.getTaskById(taskId).getId(), "Id не равны");
        assertEquals(name, taskManager.getTaskById(taskId).getName(), "name не равны");
        assertEquals(desc, taskManager.getTaskById(taskId).getDescription(), "Description не равны");
        assertEquals(status, taskManager.getTaskById(taskId).getStatus(), "Status не равны");
    }

    @Test
    void returnAfterRemoveTask() {
        Task task1 = new Task("Купить овощи", "важное дело", Status.NEW);
        Task task2 = new Task("Купить бобы", "важное дело", Status.NEW);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        assertEquals(3, taskManager.getAllTasks().size());
        taskManager.removeTaskById(1);
        assertEquals(2, taskManager.getAllTasks().size(), "Не удален таск");
        assertNotEquals(1, taskManager.getAllTasks().get(0), "Не удален нужный таск");
        taskManager.removeTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "Не пустой");
    }

    @Test
    void generateIdAfterAddTaskWithCustomId() {
        Task task1 = new Task(100, "Купить овощи", "важное дело", Status.NEW);
        taskManager.addNewTask(task1);
        assertNotEquals(100, task.getId(), "Id равны");
    }
}