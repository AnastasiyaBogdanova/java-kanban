package manager;

import exception.ManagerSaveException;
import file.FileConverter;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    final String title = "id,type,name,status,description,epic\n";

    private FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write(title);
            for (Task task : getAllTasks()) {
                bufferedWriter.write(FileConverter.toCSV(
                                task.getId(),
                                TaskType.TASK,
                                task.getName(),
                                task.getStatus(),
                                task.getDescription()
                        )
                );
            }
            for (Epic epic : getAllEpics()) {
                bufferedWriter.write(FileConverter.toCSV(
                                epic.getId(),
                                TaskType.EPIC,
                                epic.getName(),
                                epic.getStatus(),
                                epic.getDescription()
                        )
                );
            }
            for (Subtask subTask : getAllSubTasks()) {
                bufferedWriter.write(FileConverter.toCSV(
                                subTask.getId(),
                                TaskType.SUBTASK,
                                subTask.getName(),
                                subTask.getStatus(),
                                subTask.getDescription(),
                                subTask.getEpicId()
                        )
                );
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
                String type = line.split(",")[1];
                if (type.equals("SUBTASK")) {
                    fileBackedTaskManager.subTaskMap.put(
                            Integer.parseInt(line.split(",")[0]),
                            FileConverter.subtaskFromString(line)
                    );
                    maxId = getMaxId(Integer.parseInt(line.split(",")[0]), maxId);// не вынесено в переменную, так как в заголовке строка , а не число
                } else if (type.equals("TASK")) {
                    fileBackedTaskManager.taskMap.put(
                            Integer.parseInt(line.split(",")[0]),
                            FileConverter.taskFromString(line)
                    );
                    maxId = getMaxId(Integer.parseInt(line.split(",")[0]), maxId);
                } else if (type.equals("EPIC")) {
                    fileBackedTaskManager.epicMap.put(
                            Integer.parseInt(line.split(",")[0]),
                            FileConverter.epicFromString(line)
                    );
                    maxId = getMaxId(Integer.parseInt(line.split(",")[0]), maxId);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении" + e.getMessage());
        }
        fileBackedTaskManager.taskId = maxId + 1;
        return fileBackedTaskManager;
    }

    private static int getMaxId(int id, int maxId) {
        if (id > maxId) {
            maxId = id;
        }
        return maxId;
    }

}
