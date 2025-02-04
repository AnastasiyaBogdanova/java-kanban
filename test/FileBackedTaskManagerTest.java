import org.junit.jupiter.api.Test;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import task.Status;
import manager.*;

import java.util.List;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileManager;


    @Test
    void addNewTasksAndLoadFromFile() throws IOException {
        File tempFile = File.createTempFile("file", "_1");
        fileManager = new FileBackedTaskManager(tempFile);
        fileManager.addNewTask(new Task("Помыть посуду", "Она в раковине", Status.NEW));
        fileManager.addNewTask(new Task("Помыть пол", "Мистер пропер в шкафу", Status.NEW));

        FileBackedTaskManager newFileManager = fileManager.loadFromFile(tempFile);
        ArrayList<Task> tasks = newFileManager.getAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество тасков.");

    }

    @Test
    void addEmptyFile() throws IOException {
        File tempFile = File.createTempFile("file", "_1");
        fileManager = new FileBackedTaskManager(tempFile);
        fileManager.save();

        FileBackedTaskManager newFileManager = fileManager.loadFromFile(tempFile);
        ArrayList<Task> tasks = newFileManager.getAllTasks();

        assertEquals(0, tasks.size(), "Неверное количество тасков.");

    }
}
