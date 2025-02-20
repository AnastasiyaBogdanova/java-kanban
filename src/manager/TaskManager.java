package manager;

import exception.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {

    //получение списка всех объектов
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubTasks();

    //методы удаления объектов
    void removeTasks() throws ManagerSaveException;

    void removeEpics() throws ManagerSaveException;

    void removeSubTasks() throws ManagerSaveException;

    //получение объектов по id
    Optional<Task> getTaskById(int id);

    Optional<Subtask> getSubTaskById(int id);

    Optional<Epic> getEpicById(int id);

    //методы создания новых объектов
    Task addNewTask(Task task);

    Epic addNewEpic(Epic epic);

    Subtask addNewSubTask(Subtask subTask);

    //обновление объектов
    Task updateTask(Task task);

    Subtask updateSubTask(Subtask subTask);

    Epic updateEpic(Epic epic);

    //методы удаления по id
    Task removeTaskById(int id);

    Epic removeEpicById(int id);

    Subtask removeSubTaskById(int id);

    //получение всех сабтасков эпика
    ArrayList<Subtask> getSubtasksByEpic(int epicId);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

}
