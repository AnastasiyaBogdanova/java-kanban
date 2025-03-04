import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.InvalidTaskStartTimeException;
import manager.TaskManager;
import task.Status;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws InvalidTaskStartTimeException {
        Task task = new Task("Помыть посуду", "Она в раковине", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now().minusHours(1));
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        String g = gson.toJson(task);
        System.out.println(g);
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
