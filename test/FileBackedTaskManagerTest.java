import manager.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileManager;


    @Test
    void addNewTasksAndLoadFromFile() throws IOException {
        File tempFile = File.createTempFile("file", "_1");
        fileManager = FileBackedTaskManager.loadFromFile(tempFile);
        fileManager.addNewTask(new Task("Помыть посуду", "Она в раковине", Status.NEW));
        fileManager.addNewTask(new Task("Помыть пол", "Мистер пропер в шкафу", Status.NEW));

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

        assertEquals(fileManager.getSubTaskById(1), newFileManager.getSubTaskById(1), "Не равны");

    }

}
