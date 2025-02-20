import exception.InvalidTaskStartTimeException;
import manager.FileBackedTaskManager;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException, InvalidTaskStartTimeException {
        File tempFile = File.createTempFile("file", "_1");
        FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(tempFile);
        fileManager.addNewTask(new Task("Помыть посуду", "Она в раковине", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusHours(1)));
        fileManager.addNewTask(new Task("Помыть пол", "Мистер пропер в шкафу", Status.NEW, Duration.ofMinutes(50), LocalDateTime.now().plusMinutes(20)));
        //fileManager.addNewTask(new Task("Помыть пол", "Мистер пропер в шкафу", Status.NEW, Duration.ofMinutes(300), LocalDateTime.now().minusHours(2)));
        //fileManager.addNewTask(new Task("Помыть пол666", "Мистер пропер в шкафу", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusDays(38)));
        Epic epic = new Epic("Посетить магазин", "Список покупок ниже");
        fileManager.addNewEpic(epic);
        fileManager.addNewSubTask(new Subtask("Хлеб", "Черный", Status.DONE, epic.getId(), Duration.ofMinutes(8), LocalDateTime.now()));
        fileManager.addNewSubTask(new Subtask("Булка", "С маком", Status.NEW, epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().minusHours(2)));
        System.out.println(fileManager.getPrioritizedTasks());
        System.out.println(fileManager.getEpicById(epic.getId()).get().getEndTime());
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
