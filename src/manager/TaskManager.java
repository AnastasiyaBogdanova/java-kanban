package manager;

import java.util.ArrayList;
import java.util.List;

import exception.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;

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
    Task getTaskById(int id);

    Subtask getSubTaskById(int id);

    Epic getEpicById(int id);

    //методы создания новых объектов
    Task addNewTask(Task task) throws ManagerSaveException;

    Epic addNewEpic(Epic epic) throws ManagerSaveException;

    Subtask addNewSubTask(Subtask subTask) throws ManagerSaveException;

    //обновление объектов
    Task updateTask(Task task) throws ManagerSaveException;

    Subtask updateSubTask(Subtask subTask) throws ManagerSaveException;

    Epic updateEpic(Epic epic) throws ManagerSaveException;

    //методы удаления по id
    Task removeTaskById(int id) throws ManagerSaveException;

    Epic removeEpicById(int id) throws ManagerSaveException;

    Subtask removeSubTaskById(int id) throws ManagerSaveException;

    //получение всех сабтасков эпика
    ArrayList<Subtask> getSubtasksByEpic(int epicId);

    List<Task> getHistory();

}
