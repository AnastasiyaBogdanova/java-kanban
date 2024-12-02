import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class Manager {

    private int taskId = 0;
    private HashMap<Integer, Task> taskManager;
    private HashMap<Integer, Epic> epicManager;
    private HashMap<Integer, Subtask> subTaskManager;

    public Manager() {
        taskManager = new HashMap<>();
        epicManager = new HashMap<>();
        subTaskManager = new HashMap<>();
    }

    private int getNextId() {
        return taskId++;
    }

    //получение списка всех объектов
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<Task>(taskManager.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<Epic>(epicManager.values());
    }

    public ArrayList<Subtask> getAllSubTasks() {
        return new ArrayList<Subtask>(subTaskManager.values());
    }

    //методы удаления объектов
    public HashMap<Integer, Task> removeTasks() {
        taskManager.clear();
        return taskManager;
    }

    public HashMap<Integer, Epic> removeEpics() {
        epicManager.clear();
        return epicManager;
    }

    public HashMap<Integer, Subtask> removeSubTasks() {
        subTaskManager.clear();
        return subTaskManager;
    }

    //получение объектов по id
    public Task getTaskById(int id) {
        if (taskManager.containsKey(id)) {
            return taskManager.get(id);
        } else {
            return null;
        }
    }

    public Subtask getSubTaskById(int id) {
        if (subTaskManager.containsKey(id)) {
            return subTaskManager.get(id);
        } else {
            return null;
        }
    }

    public Epic getEpicById(int id) {
        if (epicManager.containsKey(id)) {
            return epicManager.get(id);
        } else {
            return null;
        }
    }

    //методы создания новых объектов
    public Task addNewTask(Task task) {
        task.setId(getNextId());
        taskManager.put(task.getId(), task);
        return task;
    }

    public Epic addNewEpic(Epic epic) {
        epic.setId(getNextId());
        epicManager.put(epic.getId(), epic);
        return epic;
    }

    public Subtask addNewSubTask(Subtask subTask) {
        subTask.setId(getNextId());
        subTaskManager.put(subTask.getId(), subTask);
        epicManager.get(subTask.getEpicId()).setSubTask(subTask.getId());
        setEpicStatus(subTask.getEpicId());
        return subTask;
    }

    //обновление объектов
    public Task updateTask(Task task) {
        taskManager.put(task.getId(), task);
        return task;
    }

    public Subtask updateSubTask(Subtask subTask) {
        subTaskManager.put(subTask.getId(), subTask);
        setEpicStatus(subTask.getEpicId());
        return subTask;
    }

    public Epic updateEpic(Epic epic) {
        Epic oldEpic = epicManager.get(epic.getId());
        ArrayList<Integer> subtasks = oldEpic.getSubTasksId();
        epic.setSubTasksId(subtasks);
        epicManager.put(epic.getId(), epic);
        return epic;
    }

    //методы удаления по id
    public HashMap<Integer, Task> removeTaskById(int id) {
        taskManager.remove(id);
        return taskManager;
    }

    public HashMap<Integer, Epic> removeEpicById(int id) {
        ArrayList<Subtask> subtasks = getAllSubTasksInEpic(id);
        for (Subtask subtask : subtasks) {
            subTaskManager.remove(subtask.getId());
        }
        epicManager.remove(id);
        return epicManager;
    }

    public HashMap<Integer, Subtask> removeSubTaskById(int id) {
        Subtask subtask = getSubTaskById(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epicManager.get(epicId);
            epic.removeSubTask(id);
            setEpicStatus(epicId);
            subTaskManager.remove(id);
        }
        return subTaskManager;
    }

    //получение всех сабтасков эпика
    public ArrayList<Subtask> getAllSubTasksInEpic(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            ArrayList<Integer> subTasksId = epic.getSubTasksId();
            ArrayList<Subtask> subTasks = new ArrayList<>();
            if (subTasksId != null) {
                for (Integer i : subTasksId) {
                    subTasks.add(subTaskManager.get(i));
                }
                return subTasks;
            }
        }
        return null;
    }

    //метод пересчета статуса в эпике
    private void setEpicStatus(int epicId) {
        Status summaryStatus = Status.NEW;
        Epic epic = epicManager.get(epicId);
        ArrayList<Integer> subTasksId = epic.getSubTasksId();
        if (subTasksId != null) {
            boolean flagNew = false;
            boolean flagDone = false;
            boolean flagInprogress = false;
            Subtask subTask;
            for (Integer id : subTasksId) {
                subTask = getSubTaskById((int) id);
                if (subTask != null) {
                    if (subTask.getStatus().equals(Status.IN_PROGRESS)) {
                        flagInprogress = true;
                    } else if (subTask.status.equals(Status.NEW)) {
                        flagNew = true;
                    } else {
                        flagDone = true;
                    }
                }
            }
            if (!flagInprogress && !flagNew) {
                summaryStatus = Status.DONE;
            } else if (!flagInprogress && !flagDone) {
                summaryStatus = Status.NEW;
            } else {
                summaryStatus = Status.IN_PROGRESS;
            }
        }
        epic.setStatus(summaryStatus);
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
