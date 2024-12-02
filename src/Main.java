public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.addNewTask(new Task("Помыть посуду", "Она в раковине", Status.NEW));
        manager.addNewTask(new Task("Помыть пол", "Мистер пропер в шкафу", Status.NEW));

        Epic epic = manager.addNewEpic(new Epic("Подготовка к НГ", "Список дел"));
        manager.addNewSubTask(new Subtask("Купить елку", "На рынке", Status.NEW, epic.getId()));
        manager.addNewSubTask(new Subtask("Заказать игрушки", "На озоне или вб", Status.NEW, epic.getId()));

        epic = manager.addNewEpic(new Epic("Подготовка к ДР", "Список покупок"));
        manager.addNewSubTask(new Subtask("Торт", "Чародейка по акции в пятерочке", Status.NEW, epic.getId()));

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        manager.updateTask(new Task(0, "Помыть посуду", "Она в раковине", Status.IN_PROGRESS));
        manager.updateTask(new Task(1, "Помыть пол", "Мистер пропер в шкафу", Status.DONE));
        manager.updateSubTask(new Subtask(6, "Заказать игрушки", "На озоне или вб", Status.IN_PROGRESS, 5));


        System.out.println();
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());


        manager.removeTaskById(0);
        manager.removeEpicById(2);

        System.out.println();
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

    }
}
