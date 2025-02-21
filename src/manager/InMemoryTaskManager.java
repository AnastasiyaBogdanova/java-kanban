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
        subTaskMap.values().stream().forEach(prioritizedTasks::remove);
        subTaskMap.clear();
        epicMap.values().stream().forEach(epic -> {
            epic.setSubTasksId(new ArrayList<>());
            updateEpicTimeAndStatus(epic.getId());
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
        validateTask(task);
        task.setId(getNextId());
        taskMap.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic addNewEpic(Epic epic) {
        epic.setId(getNextId());
        epicMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addNewSubTask(Subtask subTask) {
        validateTask(subTask);
        subTask.setId(getNextId());
        subTaskMap.put(subTask.getId(), subTask);
        prioritizedTasks.add(subTask);
        epicMap.get(subTask.getEpicId()).getSubTasksId().add(subTask.getId());
        updateEpicTimeAndStatus(subTask.getEpicId());
        return subTask;
    }

    //обновление объектов
    @Override
    public Task updateTask(Task task) {
        validateTask(task);
        taskMap.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Subtask updateSubTask(Subtask subTask) {
        validateTask(subTask);
        subTaskMap.put(subTask.getId(), subTask);
        prioritizedTasks.add(subTask);
        updateEpicTimeAndStatus(subTask.getEpicId());
        return subTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic oldEpic = epicMap.get(epic.getId());
        ArrayList<Integer> subtasks = oldEpic.getSubTasksId();
        epic.setSubTasksId(subtasks);
        epicMap.put(epic.getId(), epic);
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
            updateEpicTimeAndStatus(epicId);
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
        Status summaryStatus;
        Epic epic = epicMap.get(epicId);
        List<Status> statusList = epic.getSubTasksId().stream().map(id ->
                subTaskMap.get(id).getStatus()).collect(Collectors.toList());
       /* if (statusList.isEmpty()) {
            summaryStatus = Status.NEW;
        } else */
        if (!statusList.contains(Status.IN_PROGRESS) && !statusList.contains(Status.NEW)) {
            summaryStatus = Status.DONE;
        } else if (!statusList.contains(Status.IN_PROGRESS) && !statusList.contains(Status.DONE)) {
            summaryStatus = Status.NEW;
        } else {
            summaryStatus = Status.IN_PROGRESS;
        }
        epic.setStatus(summaryStatus);
    }

    private void updateEpicStartTime(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (!epic.getSubTasksId().isEmpty()) {
            LocalDateTime startTime = epic.getSubTasksId().stream()
                    .map(subTaskMap::get)
                    .map(Task::getStartTime)
                    .sorted(Comparator.naturalOrder())
                    .findFirst()
                    .get();
            epic.setStartTime(startTime);
        } else {
            epic.setStartTime(null);
        }
    }

    protected void updateEpicEndTime(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (!epic.getSubTasksId().isEmpty()) {
            LocalDateTime endTime = epic.getSubTasksId().stream()
                    .map(subTaskMap::get)
                    .map(Task::calcEndTime)
                    .sorted(Comparator.naturalOrder())
                    .collect(Collectors.toList())
                    .getLast();
            epic.setEndTime(endTime);
        } else {
            epic.setEndTime(null);
        }
    }

    private void updateEpicDuration(int epicId) {
        Epic epic = epicMap.get(epicId);
        Long duration = epic.getSubTasksId().stream()
                .mapToLong(id ->
                        subTaskMap.get(id).getDuration().toMinutes()
                )
                .sum();
        epic.setDuration(Duration.ofMinutes(duration));
    }

    private void updateEpicTimeAndStatus(int epicId) {
        updateEpicStatus(epicId);
        updateEpicStartTime(epicId);
        updateEpicEndTime(epicId);
        updateEpicDuration(epicId);
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new LinkedHashSet<>(prioritizedTasks);
    }

    private void validateTask(Task task) throws InvalidTaskStartTimeException {
        Optional intersectionTask =
                prioritizedTasks.stream()
                        .filter(t1 -> (!isTaskEquals(t1, task) && hasIntersection(t1, task)))
                        .findAny();
        if (intersectionTask.isPresent()) {
            throw new InvalidTaskStartTimeException("Задача имеет общеее пересечение по времени");
        }
    }

    private boolean isTaskEquals(Task t1, Task t2) {
        return t1.equals(t2);
    }


    private boolean isStartTimeInside(Task t1, Task t2) {
        return t2.getStartTime().isAfter(t1.getStartTime())
                && t2.getStartTime().isBefore(t1.calcEndTime());

    }

    private boolean isEndTimeInside(Task t1, Task t2) {
        return t2.calcEndTime().isAfter(t1.getStartTime())
                && t2.calcEndTime().isBefore(t1.calcEndTime());
    }

    private boolean isStartAndEndTimeOutside(Task t1, Task t2) {
        return t2.getStartTime().isBefore(t1.getStartTime())
                && t2.calcEndTime().isAfter(t1.calcEndTime());
    }

    private boolean isTimeEquals(Task t1, Task t2) {
        return t2.getStartTime().isEqual(t1.calcEndTime())
                || t2.calcEndTime().isEqual(t1.getStartTime());
    }

    private boolean hasIntersection(Task t1, Task t2) {
        return isStartTimeInside(t1, t2)
                || isEndTimeInside(t1, t2)
                || isStartAndEndTimeOutside(t1, t2)
                || isTimeEquals(t1, t2);
    }


}
