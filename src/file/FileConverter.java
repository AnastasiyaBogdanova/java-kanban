package file;

import task.*;

public class FileConverter {

    public static String toCSV(int id, TaskType task, String name, Status status, String description) {
        return String.format("%s,%s,%s,%s,%s\n", id, task, name, status, description);
    }

    public static String toCSV(int id, TaskType task, String name, Status status, String description, int epicId) {
        return String.format("%s,%s,%s,%s,%s,%s\n", id, task, name, status, description, epicId);
    }

    public static Task taskFromString(String value) {
        String[] params = value.split(",");
        return new Task(Integer.parseInt(params[0]), params[2], params[4], Status.valueOf(params[3]));
    }

    public static Subtask subtaskFromString(String value) {
        String[] params = value.split(",");
        return new Subtask(
                Integer.parseInt(params[0]),
                params[2],
                params[4],
                Status.valueOf(params[3]),
                Integer.parseInt(params[5])
        );
    }

    public static Epic epicFromString(String value) {
        String[] params = value.split(",");
        return new Epic(Integer.parseInt(params[0]), params[2], params[4]);
    }
}
