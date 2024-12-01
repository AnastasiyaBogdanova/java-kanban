
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Objects;

public class Manager {

    int taskId=0;
    HashMap<Integer,Task> taskManager;
    HashMap<Integer,Epic> epicManager;
    HashMap<Integer,Subtask> subTaskManager;

    public Manager() {
        taskManager = new HashMap<>();
        epicManager = new HashMap<>();
        subTaskManager = new HashMap<>();
    }

    //получение всех объектов
    Collection<Task> getAllTask(){
        return taskManager.values();
    }
    HashMap<Integer,Epic> getAllEpic(){
        return epicManager;
    }
    HashMap<Integer,Subtask> getAllSubTask(){
        return subTaskManager;
    }

    //методы удаления по id
    void removeTaskById(int id){
         taskManager.remove(id);
    }
    void removeEpicById(int id){
        epicManager.remove(id);
    }
    void removeSubTaskById(int id){
        subTaskManager.remove(id);
    }

    //методы добавления новых объектов
    Task addNewTask(Task task){
        taskManager.put(taskId,task);
        task.setId(taskId);
        taskId++;
        return task;
    }
    Epic addNewEpic(Epic epic){
        epicManager.put(taskId,epic);
        epic.setId(taskId);
        taskId++;
        return epic;
    }
    Subtask addNewSubTask(Subtask subtask){
        subTaskManager.put(taskId,subtask);
        subtask.setId(taskId);
        taskId++;
        Epic epic = epicManager.get(subtask.epicId);
        epic.setSubTask(subtask);
        System.out.println(epicManager.get(subtask.epicId));
        return subtask;
    }
    //получение объектов по id
    Task getTaskById(int id){
        if (taskManager.containsKey(id)) {
            return taskManager.get(id);
        } else {
            return null;
        }
    }
    Subtask getSubTaskById(int id){
        if (subTaskManager.containsKey(id)) {
            return subTaskManager.get(id);
        } else {
            return null;
        }
    }
    Epic getEpicById(int id){
        if (epicManager.containsKey(id)) {
            return epicManager.get(id);
        } else {
            return null;
        }
    }


    //получение всех сабтасков эпика
    ArrayList<Subtask> getAllSubTasksInEpic(int epicId){
        if (getEpicById(epicId) != null){
            return  getEpicById(epicId).subTasks;
        }
        return  null;
    }
    //обновление объектов
    public void updateTask(Task task) {
        taskManager.put(taskId, task);
    }
    public void updateSubTask(Subtask subTask) {
        subTaskManager.put(taskId, subTask);
        getEpicById(subTask.epicId).setStatus(subTask.status);
    }
    public void updateEpic(Epic epic) {
        epicManager.put(taskId, epic);
    }

    @Override
    public String toString() {
        return "Manager{" +
                "taskManager=" + taskManager +
                ", epicManager=" + epicManager +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Manager manager)) return false;
        return taskId == manager.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
