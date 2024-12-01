
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.addNewTask(new Task("Помыть посуду", "Она в раковине", Status.NEW));
        System.out.println(manager.addNewTask(new Task("Помыть пол", "Мистер пропер в шкафу", Status.NEW)));
        Epic epic = manager.addNewEpic(new Epic("Подготовка к НГ", "Список дел", Status.IN_PROGRESS));
        manager.addNewSubTask(new Subtask("Купить елку", "На рынке", Status.DONE,epic.getId()));
        manager.addNewSubTask(new Subtask("Заказать игрушки", "На озоне или вб", Status.IN_PROGRESS,epic.getId()));
        manager.addNewSubTask(new Subtask("Украсить дом", "Гирлянда в шкафу", Status.NEW,epic.getId()));
        manager.addNewSubTask(new Subtask("Выкинуть елку", "На авито", Status.DONE,epic.getId()));
//manager.removeEpicById(3);
        System.out.println(epic);
//        epic = manager.addNewEpic(new Epic("Отпуск", "Список покупок к отпуску", Status.DONE));
//        System.out.println(epic);
       // manager.addNewSubTask(new Subtask("Купальник", "На вб", Status.NEW,epId));
       // manager.addNewSubTask(new Subtask("Шляпа", "Поискать в магазине", Status.NEW,epId));
        //System.out.println(manager.addNewTask());

    }
}
