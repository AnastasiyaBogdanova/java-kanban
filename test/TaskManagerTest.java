import exception.InvalidTaskStartTimeException;
import exception.ManagerSaveException;
import manager.FileBackedTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Task task;
    private static int epicId;
    private static int taskId;
    private FileBackedTaskManager fileManager;

    protected TaskManager taskManager;

    public abstract T getTaskManager();

    @BeforeEach
    void setUp() throws ManagerSaveException {
        taskManager = getTaskManager();
        task = new Task("Купить хлеб",
                "важное дело",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.now().minusHours(8)
        );
        epic1 = new Epic("Помыть кота", "с шампунем");
        epicId = taskManager.addNewEpic(epic1).getId();
        taskId = taskManager.addNewTask(task).getId();
    }

    @Test
    void addNewTaskAndReturnByIdAndCheckEquals() {
        final Optional<Task> savedTask = taskManager.getTaskById(taskId);
        System.out.println(savedTask);
        System.out.println(savedTask.isPresent());

        assertNotNull(savedTask.get(), "таск не найден.");
        assertEquals(task, savedTask.get(), "таски не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "таски не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество тасков.");
        assertEquals(task, tasks.get(0), "таски не совпадают.");
    }

    @Test
    void addNewSubTaskAndReturnByIdAndCheckEquals() throws ManagerSaveException {
        Subtask subtask = new Subtask("нарезать салат",
                "из овощей",
                Status.NEW, epicId,
                Duration.ofMinutes(10),
                LocalDateTime.now()
        );
        final int subtaskId = taskManager.addNewSubTask(subtask).getId();

        final Optional<Subtask> savedSubTask = taskManager.getSubTaskById(subtaskId);

        assertNotNull(savedSubTask, "сабтаск не найден.");
        assertEquals(subtask, savedSubTask.get(), "сабтаски не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubTasks();

        assertNotNull(subtasks, "сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасков.");
        assertEquals(subtask, subtasks.get(0), "сабтаски не совпадают.");
    }

    @Test
    void addNewEpicAndReturnByIdAndCheckEquals() {
        final Optional<Epic> savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic.get(), "эпик не найден.");
        assertEquals(epic1, savedEpic.get(), "эпики не совпадают.");

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
        assertEquals(taskId, taskManager.getTaskById(taskId).get().getId(), "Id не равны");
        assertEquals(name, taskManager.getTaskById(taskId).get().getName(), "name не равны");
        assertEquals(desc, taskManager.getTaskById(taskId).get().getDescription(), "Description не равны");
        assertEquals(status, taskManager.getTaskById(taskId).get().getStatus(), "Status не равны");
    }

    @Test
    void returnAfterRemoveTask() throws ManagerSaveException {
        taskManager.removeEpics();
        taskManager.removeTasks();
        Task task1 = new Task("Купить овощи",
                "важное дело",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.now().plusDays(56)
        );
        Task task2 = new Task("Купить бобы",
                "важное дело",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.now().plusWeeks(3)
        );
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        assertEquals(2, taskManager.getAllTasks().size());
        taskManager.removeTaskById(task1.getId());
        assertEquals(1, taskManager.getAllTasks().size(), "Не удален таск");
        assertNotEquals(1, taskManager.getAllTasks().get(0), "Не удален нужный таск");
        taskManager.removeTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "Не пустой");
    }

    @Test
    void generateIdAfterAddTaskWithCustomId() throws ManagerSaveException {
        Task task1 = new Task(100, "Купить овощи", "важное дело",
                Status.NEW, Duration.ofMinutes(80), LocalDateTime.now());
        taskManager.addNewTask(task1);
        assertNotEquals(100, task.getId(), "Id равны");
    }

    @Test
    void removeSubTaskFromEpic() throws ManagerSaveException {
        Epic epic = new Epic("Купить овощи", "важное дело");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Купить бобы", "важное дело", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Купить помидоры", "важное дело", Status.NEW, epic.getId());
        Subtask subtask3 = new Subtask("Купить огурцы", "важное дело", Status.NEW, epic.getId());
        taskManager.addNewSubTask(subtask1);
        taskManager.addNewSubTask(subtask2);
        taskManager.addNewSubTask(subtask3);
        assertEquals(3, taskManager.getAllSubTasks().size());
        taskManager.removeSubTaskById(subtask2.getId());
        assertEquals(2, taskManager.getAllSubTasks().size(), "Не удален таск");
    }

    @Test
    void calcEpicStatus() throws ManagerSaveException {
        Epic epic = new Epic("Купить овощи", "важное дело");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Купить бобы", "важное дело", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Купить помидоры", "важное дело", Status.NEW, epic.getId());

        taskManager.addNewSubTask(subtask1);
        taskManager.addNewSubTask(subtask2);

        assertEquals(Status.NEW,
                taskManager.getEpicById(epic.getId()).get().getStatus(),
                "Статус не равен NEW");

        taskManager.removeSubTasks();
        subtask1 = new Subtask("Купить бобы", "важное дело", Status.DONE, epic.getId());
        subtask2 = new Subtask("Купить помидоры", "важное дело", Status.DONE, epic.getId());
        taskManager.addNewSubTask(subtask1);
        taskManager.addNewSubTask(subtask2);
        assertEquals(Status.DONE,
                taskManager.getEpicById(epic.getId()).get().getStatus(),
                "Статус не равен DONE");

        taskManager.removeSubTasks();
        subtask1 = new Subtask("Купить бобы", "важное дело", Status.DONE, epic.getId());
        subtask2 = new Subtask("Купить помидоры", "важное дело", Status.NEW, epic.getId());
        taskManager.addNewSubTask(subtask1);
        taskManager.addNewSubTask(subtask2);
        assertEquals(Status.IN_PROGRESS,
                taskManager.getEpicById(epic.getId()).get().getStatus(),
                "Статус не равен DONE");

        taskManager.removeSubTasks();
        subtask1 = new Subtask("Купить бобы", "важное дело", Status.IN_PROGRESS, epic.getId());
        subtask2 = new Subtask("Купить помидоры", "важное дело", Status.IN_PROGRESS, epic.getId());
        taskManager.addNewSubTask(subtask1);
        taskManager.addNewSubTask(subtask2);
        assertEquals(Status.IN_PROGRESS,
                taskManager.getEpicById(epic.getId()).get().getStatus(),
                "Статус не равен DONE");
    }

    @Test
    void testExceptionTimeForTasks() {
        //taskManager = Managers.getDefault();
        task1 = new Task("Купить овощи",
                "важное дело",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.now().plusMinutes(5)
        );
        task2 = new Task("Купить бобы",
                "важное дело",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.now()
        );
        taskManager.addNewTask(task2);
        assertThrows(InvalidTaskStartTimeException.class,() -> taskManager.addNewTask(task1));
    }

    @Test
    void testPrioritizedTasks() {
        task1 = new Task("Купить овощи",
                "важное дело",
                Status.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.now().plusMinutes(55)
        );
        task2 = new Task("Купить бобы",
                "важное дело",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.now()
        );
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task1);
        Task firstTask = taskManager.getPrioritizedTasks().stream().collect(Collectors.toList()).getFirst();

        assertEquals(task.getId(), firstTask.getId());
    }

    @Test
    void addNewTasksAndLoadFromFile() throws IOException {
        File tempFile = File.createTempFile("file", "_1");
        fileManager = FileBackedTaskManager.loadFromFile(tempFile);
        fileManager.addNewTask(new Task("Помыть посуду", "Она в раковине", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now().plusDays(5)));
        fileManager.addNewTask(new Task("Помыть пол", "Мистер пропер в шкафу", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now()));

        FileBackedTaskManager newFileManager = fileManager.loadFromFile(tempFile);
        ArrayList<Task> tasks = newFileManager.getAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество тасков.");

    }

    @Test
    void addEmptyFile() throws IOException {
        File tempFile = File.createTempFile("file", "_1");
        fileManager = FileBackedTaskManager.loadFromFile(tempFile);

        FileBackedTaskManager newFileManager = fileManager.loadFromFile(tempFile);
        ArrayList<Task> tasks = newFileManager.getAllTasks();

        assertEquals(0, tasks.size(), "Неверное количество тасков.");

    }

    @Test
    void checkEqualsSubtasks() throws IOException {
        File tempFile = File.createTempFile("file", "_1");
        fileManager = FileBackedTaskManager.loadFromFile(tempFile);
        Epic epic = new Epic("Посетить магазин", "Список покупок ниже");
        fileManager.addNewEpic(epic);
        fileManager.addNewSubTask(new Subtask("Хлеб", "Черный", Status.NEW, epic.getId()));
        FileBackedTaskManager newFileManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(fileManager.getSubTaskById(2).get(), newFileManager.getSubTaskById(2).get(), "Не равны");

    }
}