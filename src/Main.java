import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManager.addNewTask(new Task("Помыть посуду", "Она в раковине", Status.NEW));
        inMemoryTaskManager.addNewTask(new Task("Помыть пол", "Мистер пропер в шкафу", Status.NEW));

        Epic epic = inMemoryTaskManager.addNewEpic(new Epic("Подготовка к НГ", "Список дел"));
        inMemoryTaskManager.addNewSubTask(new Subtask("Купить елку", "На рынке", Status.NEW, epic.getId()));
        inMemoryTaskManager.addNewSubTask(new Subtask("Заказать игрушки", "На озоне или вб", Status.NEW, epic.getId()));
        inMemoryTaskManager.addNewSubTask(new Subtask("Заказать подарки", "На озоне или вб", Status.NEW, epic.getId()));

        inMemoryTaskManager.addNewEpic(new Epic("Подготовка к ДР", "Список покупок"));

        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.updateTask(new Task(1, "Помыть посуду изменено2222", "Она в раковине", Status.NEW));
        inMemoryTaskManager.getEpicById(2);
        inMemoryTaskManager.getTaskById(0);
        inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.getEpicById(2);
        inMemoryTaskManager.getEpicById(6);
        inMemoryTaskManager.getSubTaskById(5);
        inMemoryTaskManager.getSubTaskById(5);
        inMemoryTaskManager.getSubTaskById(3);
        inMemoryTaskManager.getSubTaskById(4);
        inMemoryTaskManager.getEpicById(2);

        System.out.println("История без повторов:");
        for (Task task : inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println();
        inMemoryTaskManager.removeTaskById(0);
        System.out.println("Удалили таск id=0:");
        for (Task task : inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println();
        inMemoryTaskManager.removeEpicById(2);
        System.out.println("Удалили эпик id=2:");
        for (Task task : inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println();
        inMemoryTaskManager.updateTask(new Task(1, "Помыть посуду изменено", "Она в раковине", Status.NEW));
        for (Task task : inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
