import manager.FileBackedTaskManager;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        File tempFile = File.createTempFile("file", "_1");

        FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(tempFile);
        fileManager.addNewTask(new Task("Помыть посуду", "Она в раковине", Status.NEW));
        fileManager.addNewTask(new Task("Помыть пол", "Мистер пропер в шкафу", Status.NEW));
        Epic epic = new Epic("Посетить магазин", "Список покупок ниже");
        fileManager.addNewEpic(epic);
        fileManager.addNewSubTask(new Subtask("Хлеб", "Черный", Status.NEW, epic.getId()));
        fileManager.addNewSubTask(new Subtask("Булка", "С маком", Status.NEW, epic.getId()));
        FileBackedTaskManager newFileManager = FileBackedTaskManager.loadFromFile(tempFile);
        System.out.println("новый, создан из файла");
        printAllTasks(newFileManager);
        System.out.println();
        FileBackedTaskManager newfileManager = FileBackedTaskManager.loadFromFile(tempFile);
        System.out.println("старый, создан добавлением в файл");
        printAllTasks(newfileManager);
    }

    public static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }
        System.out.println("История:");

        for (Object o : manager.getHistory()) {
            System.out.println(o);
        }
    }
}
