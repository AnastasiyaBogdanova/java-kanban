package manager;

import exception.InvalidTaskStartTimeException;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int taskId = 0;
    protected HashMap<Integer, Task> taskMap;
    protected HashMap<Integer, Epic> epicMap;
    protected HashMap<Integer, Subtask> subTaskMap;
    private HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
    public void removeTasks() {
        taskMap.values().stream().forEach(task ->
                historyManager.remove(task.getId())
        );
        taskMap.values().stream().forEach(prioritizedTasks::remove);
        taskMap.clear();
    }

    @Override
    public void removeEpics() {
        epicMap.values().stream().forEach(epic -> historyManager.remove(epic.getId()));
        epicMap.clear();
        subTaskMap.values().stream().forEach(subtask -> historyManager.remove(subtask.getId()));
        subTaskMap.values().stream().forEach(prioritizedTasks::remove);
        subTaskMap.clear();//при удаление всех эпиков удаляем все сабтаски
    }

    @Override
    public void removeSubTasks() {
        subTaskMap.clear();
        subTaskMap.values().stream().forEach(prioritizedTasks::remove);
        epicMap.values().stream().forEach(epic -> {
            epic.setSubTasksId(new ArrayList<>());
            epic.setStatus(Status.NEW);
        });
    }

    //получение объектов по id
    @Override
    public Optional<Task> getTaskById(int id) {
        Task task = taskMap.get(id);
        historyManager.add(task);
        return Optional.ofNullable(task);
    }

    @Override
    public Optional<Subtask> getSubTaskById(int id) {
        Subtask subTask = subTaskMap.get(id);
        historyManager.add(subTask);
        return Optional.ofNullable(subTask);
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epicMap.get(id);
        historyManager.add(epic);
        return Optional.ofNullable(epic);
    }

    //методы создания новых объектов
    @Override
    public Task addNewTask(Task task) {
        task.setId(getNextId());
        try {
            validateTask(task);
            taskMap.put(task.getId(), task);
            prioritizedTasks.add(task);
        } catch (InvalidTaskStartTimeException e) {
            System.out.println(e.getMessage());
        }
        return task;
    }

    @Override
    public Epic addNewEpic(Epic epic) {
        epic.setId(getNextId());
        try {
            validateTask(epic);
            epicMap.put(epic.getId(), epic);
        } catch (InvalidTaskStartTimeException e) {
            System.out.println(e.getMessage());
        }
        return epic;
    }

    @Override
    public Subtask addNewSubTask(Subtask subTask) {
        subTask.setId(getNextId());
        try {
            validateTask(subTask);
            subTaskMap.put(subTask.getId(), subTask);
            prioritizedTasks.add(subTask);
            epicMap.get(subTask.getEpicId()).getSubTasksId().add(subTask.getId());
            updateEpicStatus(subTask.getEpicId());
            setStartDateTime(subTask.getEpicId());
            setEndDateTime(subTask.getEpicId());
        } catch (InvalidTaskStartTimeException e) {
            System.out.println(e.getMessage());
        }
        return subTask;
    }

    //обновление объектов
    @Override
    public Task updateTask(Task task) {
        try {
            validateTask(task);
            taskMap.put(task.getId(), task);
            prioritizedTasks.add(task);
        } catch (InvalidTaskStartTimeException e) {
            System.out.println(e.getMessage());
        }
        return task;
    }

    @Override
    public Subtask updateSubTask(Subtask subTask) {

        try {
            validateTask(subTask);
            subTaskMap.put(subTask.getId(), subTask);
            prioritizedTasks.add(subTask);
            updateEpicStatus(subTask.getEpicId());
            setStartDateTime(subTask.getEpicId());
            setEndDateTime(subTask.getEpicId());
        } catch (InvalidTaskStartTimeException e) {
            System.out.println(e.getMessage());
        }
        return subTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        try {
            validateTask(epic);
            Epic oldEpic = epicMap.get(epic.getId());
            ArrayList<Integer> subtasks = oldEpic.getSubTasksId();
            epic.setSubTasksId(subtasks);
            epicMap.put(epic.getId(), epic);
        } catch (InvalidTaskStartTimeException e) {
            System.out.println(e.getMessage());
        }

        return epic;
    }

    //методы удаления по id
    @Override
    public Task removeTaskById(int id) {
        historyManager.remove(id);
        prioritizedTasks.remove(taskMap.get(id));
        return taskMap.remove(id);
    }

    @Override
    public Epic removeEpicById(int id) {
        getSubtasksByEpic(id)
                .stream()
                .forEach(subtask -> {
                    prioritizedTasks.remove(subTaskMap.get(subtask.getId()));
                    subTaskMap.remove(subtask.getId());
                    historyManager.remove(subtask.getId());

                });

        historyManager.remove(id);
        return epicMap.remove(id);
    }

    @Override
    public Subtask removeSubTaskById(int id) {
        Subtask subtask = subTaskMap.get(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epicMap.get(epicId);
            epic.removeSubTask(id);
            updateEpicStatus(epicId);
            setStartDateTime(epicId);
            setEndDateTime(epicId);
            historyManager.remove(id);
            prioritizedTasks.remove(subTaskMap.get(id));
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
        List<Status> statusList = epic.getSubTasksId().stream().map(id ->
                subTaskMap.get(id).getStatus()).collect(Collectors.toList());
        if (!statusList.contains(Status.IN_PROGRESS) && !statusList.contains(Status.NEW))
            summaryStatus = Status.DONE;
        else if (!statusList.contains(Status.IN_PROGRESS) && !statusList.contains(Status.DONE))
            summaryStatus = Status.NEW;
        else
            summaryStatus = Status.IN_PROGRESS;

        epic.setStatus(summaryStatus);
    }

    private void setStartDateTime(int epicId) {
        Epic epic = epicMap.get(epicId);
        LocalDateTime startTime = epic.getSubTasksId().stream()
                .map(subTaskMap::get)
                .map(Task::getStartTime)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList())
                .getFirst();
        epic.setStartTime(startTime);
    }

    private void setEndDateTime(int epicId) {
        Epic epic = epicMap.get(epicId);
        LocalDateTime endTime = epic.getSubTasksId().stream()
                .map(subTaskMap::get)
                .map(Task::getEndTime)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList())
                .getLast();
        Duration duration = Duration.between(epic.getStartTime(), endTime);
        epic.setDuration(duration);
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new LinkedHashSet<>(prioritizedTasks);
    }

    private void validateTask(Task task) throws InvalidTaskStartTimeException {
        List<Task> collected = prioritizedTasks.stream()
                .filter(task1 ->
                        (task.getStartTime().isAfter(task1.getStartTime())
                                && task.getStartTime().isBefore(task1.getEndTime())
                        )
                                || (task.getEndTime().isAfter(task1.getStartTime())
                                && task.getEndTime().isBefore(task1.getEndTime())
                        ) || (task.getStartTime().isBefore(task1.getStartTime())
                                && task.getEndTime().isAfter(task1.getEndTime())
                        )
                )
                .collect(Collectors.toList()
                );


        if (!collected.isEmpty()) {
            throw new InvalidTaskStartTimeException(
                    "Задача id=" + task.getId()
                            + " имеет общеее пересечение времени с задачами "
                            + collected.stream().map(c -> c.getId()).collect(Collectors.toList())
            );
        }
    }

}
