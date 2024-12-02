import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int taskId = 0;
    private HashMap<Integer, Task> taskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, Subtask> subTaskMap;

    public Manager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
    }

    private int getNextId() {
        return taskId++;
    }

    //получение списка всех объектов
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<Task>(taskMap.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<Epic>(epicMap.values());
    }

    public ArrayList<Subtask> getAllSubTasks() {
        return new ArrayList<Subtask>(subTaskMap.values());
    }

    //методы удаления объектов
    public void removeTasks() {
        taskMap.clear();
    }

    public void removeEpics() {
        epicMap.clear();
        subTaskMap.clear();//при удаление всех эпиков удаляем все сабтаски
    }

    public void removeSubTasks() {
        for (Integer i : subTaskMap.keySet()) {
            updateEpicStatus(subTaskMap.get(i).getEpicId());
        }
        subTaskMap.clear();
    }

    //получение объектов по id
    public Task getTaskById(int id) {
        if (taskMap.containsKey(id)) {
            return taskMap.get(id);
        } else {
            return null;
        }
    }

    public Subtask getSubTaskById(int id) {
        if (subTaskMap.containsKey(id)) {
            return subTaskMap.get(id);
        } else {
            return null;
        }
    }

    public Epic getEpicById(int id) {
        if (epicMap.containsKey(id)) {
            return epicMap.get(id);
        } else {
            return null;
        }
    }

    //методы создания новых объектов
    public Task addNewTask(Task task) {
        task.setId(getNextId());
        taskMap.put(task.getId(), task);
        return task;
    }

    public Epic addNewEpic(Epic epic) {
        epic.setId(getNextId());
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    public Subtask addNewSubTask(Subtask subTask) {
        subTask.setId(getNextId());
        subTaskMap.put(subTask.getId(), subTask);
        epicMap.get(subTask.getEpicId()).getSubTasksId().add(subTask.getId());
        //        setSubTask(subTask.getId());
        updateEpicStatus(subTask.getEpicId());
        return subTask;
    }

    //обновление объектов
    public Task updateTask(Task task) {
        taskMap.put(task.getId(), task);
        return task;
    }

    public Subtask updateSubTask(Subtask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
        return subTask;
    }

    public Epic updateEpic(Epic epic) {
        Epic oldEpic = epicMap.get(epic.getId());
        ArrayList<Integer> subtasks = oldEpic.getSubTasksId();
        epic.setSubTasksId(subtasks);
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    //методы удаления по id
    public Task removeTaskById(int id) {
        return taskMap.remove(id);
    }

    public Epic removeEpicById(int id) {
        ArrayList<Subtask> subtasks = getSubtasksByEpic(id);
        for (Subtask subtask : subtasks) {
            subTaskMap.remove(subtask.getId());
        }
        return epicMap.remove(id);
    }

    public Subtask removeSubTaskById(int id) {
        Subtask subtask = getSubTaskById(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epicMap.get(epicId);
            epic.removeSubTask(id);
            updateEpicStatus(epicId);
        }
        return subTaskMap.remove(id);
    }

    //получение всех сабтасков эпика
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic != null) {
            ArrayList<Subtask> subTasks = new ArrayList<>();
            for (Integer i : epic.getSubTasksId()) {
                subTasks.add(subTaskMap.get(i));
            }
            return subTasks;
        }
        return null;
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
                Subtask subTask = getSubTaskById((int) id);
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
}
