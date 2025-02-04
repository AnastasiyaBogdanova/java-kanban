package manager;

import exception.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    final String title = "id,type,name,status,description,epic\n";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        try {
            List<String> lines = new ArrayList<>();
            lines.add(title);
            List<Task> tasks = getAllTasks();
            List<Subtask> subTasks = getAllSubTasks();
            List<Epic> epics = getAllEpics();
            for (Task task : tasks) {
                lines.add(task.toString());
            }
            for (Epic epic : epics) {
                lines.add(epic.toString());
            }
            for (Subtask subTask : subTasks) {
                lines.add(subTask.toString());
            }
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                for (String line : lines) {
                    bufferedWriter.write(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
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

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        List<String> lines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                lines.add(line);
            }
        }
        int maxId = 0;
        int id;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String type = line.split(",")[1];
            id = Integer.parseInt(line.split(",")[0]);
            if (type.equals("SUBTASK")) {
                fileBackedTaskManager.subTaskMap.put(Integer.parseInt(line.split(",")[0]), Subtask.fromString(line));
            } else if (type.equals("TASK")) {
                fileBackedTaskManager.taskMap.put(Integer.parseInt(line.split(",")[0]), Task.fromString(line));
            } else if (type.equals("EPIC")) {
                fileBackedTaskManager.epicMap.put(Integer.parseInt(line.split(",")[0]), Epic.fromString(line));
            }
            if (id > maxId) {
                maxId = id;
            }
            fileBackedTaskManager.taskId = maxId + 1;
        }
        return fileBackedTaskManager;
    }
}
