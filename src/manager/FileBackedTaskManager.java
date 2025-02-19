package manager;

import exception.ManagerSaveException;
import file.CsvConverter;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    private FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write(CsvConverter.getTitle());
            for (Task task : getAllTasks()) {
                bufferedWriter.write(CsvConverter.toCSV(task));
            }
            for (Epic epic : getAllEpics()) {
                bufferedWriter.write(CsvConverter.toCSV(epic));
            }
            for (Subtask subTask : getAllSubTasks()) {
                bufferedWriter.write(CsvConverter.toCSV(subTask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении" + e.getMessage());
        }
    }

    @Override
    public Subtask addNewSubTask(Subtask subtask) throws ManagerSaveException {
        super.addNewSubTask(subtask);
        save();
        return subtask;
    }

    @Override
    public Task addNewTask(Task task) throws ManagerSaveException {
        super.addNewTask(task);
        save();
        return task;
    }

    @Override
    public Epic addNewEpic(Epic epic) throws ManagerSaveException {
        super.addNewEpic(epic);
        save();
        return epic;
    }

    @Override
    public void removeTasks() throws ManagerSaveException {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() throws ManagerSaveException {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubTasks() throws ManagerSaveException {
        super.removeSubTasks();
        save();
    }

    @Override
    public Task updateTask(Task task) throws ManagerSaveException {
        Task newTask = super.updateTask(task);
        save();
        return newTask;

    }

    @Override
    public Subtask updateSubTask(Subtask subTask) throws ManagerSaveException {
        Subtask newSubTask = super.updateSubTask(subTask);
        save();
        return newSubTask;

    }

    @Override
    public Epic updateEpic(Epic epic) throws ManagerSaveException {
        Epic newEpic = super.updateEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Task removeTaskById(int id) throws ManagerSaveException {
        Task task = super.removeTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic removeEpicById(int id) throws ManagerSaveException {
        Epic epic = super.removeEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask removeSubTaskById(int id) throws ManagerSaveException {
        Subtask subtask = super.removeSubTaskById(id);
        save();
        return subtask;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int maxId = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                String type = CsvConverter.getTaskType(line);
                switch (type) {
                    case "SUBTASK":
                        fileBackedTaskManager.subTaskMap.put(
                                CsvConverter.getId(line),
                                CsvConverter.subtaskFromString(line)
                        );
                        fileBackedTaskManager.prioritizedTasks.add(CsvConverter.subtaskFromString(line));
                        // не вынесено в переменную, так как в заголовке строка , а не число
                        maxId = getMaxId(CsvConverter.getId(line), maxId);
                        break;
                    case "TASK":
                        fileBackedTaskManager.taskMap.put(
                                CsvConverter.getId(line),
                                CsvConverter.taskFromString(line)
                        );
                        fileBackedTaskManager.prioritizedTasks.add(CsvConverter.taskFromString(line));
                        maxId = getMaxId(CsvConverter.getId(line), maxId);
                        break;
                    case "EPIC":
                        fileBackedTaskManager.epicMap.put(
                                CsvConverter.getId(line),
                                CsvConverter.epicFromString(line)
                        );
                        fileBackedTaskManager.prioritizedTasks.add(CsvConverter.epicFromString(line));
                        maxId = getMaxId(CsvConverter.getId(line), maxId);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении" + e.getMessage());
        }
        fileBackedTaskManager.taskId = maxId + 1;
        linkSubtasksToEpics(fileBackedTaskManager);
        return fileBackedTaskManager;
    }

    private static void linkSubtasksToEpics(FileBackedTaskManager fileBackedTaskManager) {
        for (Subtask s : fileBackedTaskManager.subTaskMap.values()) {
            fileBackedTaskManager.epicMap.get(s.getEpicId()).getSubTasksId().add(s.getId());
        }
    }


    private static int getMaxId(int id, int maxId) {
        if (id > maxId) {
            maxId = id;
        }
        return maxId;
    }

}
