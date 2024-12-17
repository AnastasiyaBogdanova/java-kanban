import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManager.addNewTask(new Task("Помыть посуду", "Она в раковине", Status.NEW));
        inMemoryTaskManager.addNewTask(new Task("Помыть пол", "Мистер пропер в шкафу", Status.NEW));

        Epic epic = inMemoryTaskManager.addNewEpic(new Epic("Подготовка к НГ", "Список дел"));
        inMemoryTaskManager.addNewSubTask(new Subtask("Купить елку", "На рынке", Status.NEW, epic.getId()));
        inMemoryTaskManager.addNewSubTask(new Subtask("Заказать игрушки", "На озоне или вб", Status.NEW, epic.getId()));

        epic = inMemoryTaskManager.addNewEpic(new Epic("Подготовка к ДР", "Список покупок"));
        inMemoryTaskManager.addNewSubTask(new Subtask("Торт", "Чародейка по акции в пятерочке", Status.NEW, epic.getId()));

        inMemoryTaskManager.updateTask(new Task(0, "Помыть посуду", "Она в раковине", Status.IN_PROGRESS));
        inMemoryTaskManager.updateTask(new Task(1, "Помыть пол", "Мистер пропер в шкафу", Status.DONE));
        inMemoryTaskManager.updateSubTask(new Subtask(6, "Заказать игрушки", "На озоне или вб", Status.IN_PROGRESS, 5));

        inMemoryTaskManager.removeTaskById(0);
        inMemoryTaskManager.removeEpicById(2);

        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getEpicById(5);
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getEpicById(5);
        inMemoryTaskManager.getEpicById(5);
        inMemoryTaskManager.getSubTaskById(6);
        inMemoryTaskManager.getSubTaskById(6);
        inMemoryTaskManager.getSubTaskById(6);
        inMemoryTaskManager.getSubTaskById(6);
        inMemoryTaskManager.getEpicById(5);

        inMemoryTaskManager.updateTask(new Task(1, "gjkk", "kjk", Status.NEW));
        printAllTasks(inMemoryTaskManager);

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
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
