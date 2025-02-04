package manager;

import exception.ManagerSaveException;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int taskId = 0;
    protected HashMap<Integer, Task> taskMap;
    protected HashMap<Integer, Epic> epicMap;
    protected HashMap<Integer, Subtask> subTaskMap;
    private HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
    }

    private int getNextId() {

        return taskId++;
    }

    //получение списка всех объектов
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<Task>(taskMap.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<Epic>(epicMap.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubTasks() {

        return new ArrayList<Subtask>(subTaskMap.values());
    }

    //методы удаления объектов
    @Override
    public void removeTasks() throws ManagerSaveException {
        for (Integer i : taskMap.keySet()) {
            historyManager.remove(i);
        }
        taskMap.clear();
    }

    @Override
    public void removeEpics() throws ManagerSaveException {
        for (Integer i : epicMap.keySet()) {
            historyManager.remove(i);
        }
        epicMap.clear();
        for (Integer i : subTaskMap.keySet()) {
            historyManager.remove(i);
        }
        subTaskMap.clear();//при удаление всех эпиков удаляем все сабтаски
    }

    @Override
    public void removeSubTasks() throws ManagerSaveException {
        subTaskMap.clear();
        for (Integer i : epicMap.keySet()) {
            epicMap.get(i).setSubTasksId(new ArrayList<>());
            epicMap.get(i).setStatus(Status.NEW);
        }
    }

    //получение объектов по id
    @Override
    public Task getTaskById(int id) {
        if (!taskMap.containsKey(id)) {
            return null;
        }
        Task task = taskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        if (!subTaskMap.containsKey(id)) {
            return null;
        }
        Subtask subTask = subTaskMap.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epicMap.containsKey(id)) {
            return null;
        }
        Epic epic = epicMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    //методы создания новых объектов
    @Override
    public Task addNewTask(Task task) throws ManagerSaveException {
        task.setId(getNextId());
        taskMap.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic addNewEpic(Epic epic) throws ManagerSaveException {
        epic.setId(getNextId());
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addNewSubTask(Subtask subTask) throws ManagerSaveException {
        subTask.setId(getNextId());
        subTaskMap.put(subTask.getId(), subTask);
        epicMap.get(subTask.getEpicId()).getSubTasksId().add(subTask.getId());
        updateEpicStatus(subTask.getEpicId());
        return subTask;
    }

    //обновление объектов
    @Override
    public Task updateTask(Task task) throws ManagerSaveException {
        taskMap.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask updateSubTask(Subtask subTask) throws ManagerSaveException {
        subTaskMap.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
        return subTask;
    }

    @Override
    public Epic updateEpic(Epic epic) throws ManagerSaveException {
        Epic oldEpic = epicMap.get(epic.getId());
        ArrayList<Integer> subtasks = oldEpic.getSubTasksId();
        epic.setSubTasksId(subtasks);
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    //методы удаления по id
    @Override
    public Task removeTaskById(int id) throws ManagerSaveException {
        historyManager.remove(id);
        return taskMap.remove(id);
    }

    @Override
    public Epic removeEpicById(int id) throws ManagerSaveException {
        ArrayList<Subtask> subtasks = getSubtasksByEpic(id);
        for (Subtask subtask : subtasks) {
            subTaskMap.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        historyManager.remove(id);
        return epicMap.remove(id);
    }

    @Override
    public Subtask removeSubTaskById(int id) throws ManagerSaveException {
        Subtask subtask = subTaskMap.get(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epicMap.get(epicId);
            epic.removeSubTask(id);
            updateEpicStatus(epicId);
            historyManager.remove(id);
        }
        return subTaskMap.remove(id);
    }

    //получение всех сабтасков эпика
    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic == null) {
            return null;
        }
        ArrayList<Subtask> subTasks = new ArrayList<>();
        for (Integer i : epic.getSubTasksId()) {
            subTasks.add(subTaskMap.get(i));
        }
        return subTasks;

    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //метод пересчета статуса в эпике
    private void updateEpicStatus(int epicId) {
        Status summaryStatus = Status.NEW;
        Epic epic = epicMap.get(epicId);
        ArrayList<Integer> subTasksId = epic.getSubTasksId();
        if (subTasksId != null) {
            boolean flagNew = false;
            boolean flagDone = false;
            boolean flagInprogress = false;
            for (Integer id : subTasksId) {
                Subtask subTask = subTaskMap.get(id);
                if (subTask != null) {
                    if (subTask.getStatus().equals(Status.IN_PROGRESS)) {
                        flagInprogress = true;
                    } else if (subTask.getStatus().equals(Status.NEW)) {
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

}
