package file;

import task.*;

public class CsvConverter {
    static final String title = "id,type,name,status,description,epic\n";

    public static String getTitle() {
        return title;
    }

    public static String toCSV(Subtask subtask) {
        return String.format("%s,%s,%s,%s,%s,%s\n",
                subtask.getId(),
                TaskType.SUBTASK,
                subtask.getName(),
                subtask.getStatus(),
                subtask.getDescription(),
                subtask.getEpicId()
        );
    }

    public static String toCSV(Task task) {
        return String.format("%s,%s,%s,%s,%s\n",
                task.getId(),
                TaskType.TASK,
                task.getName(),
                task.getStatus(),
                task.getDescription()
        );
    }

    public static String toCSV(Epic epic) {
        return String.format("%s,%s,%s,%s,%s\n",
                epic.getId(),
                TaskType.EPIC,
                epic.getName(),
                epic.getStatus(),
                epic.getDescription()
        );
    }


    public static Subtask subtaskFromString(String value) {
        String[] params = value.split(",");
        return new Subtask(
                Integer.parseInt(value.split(",")[0]),
                params[2],
                params[4],
                Status.valueOf(params[3]),
                Integer.parseInt(params[5])
        );
    }

    public static Task taskFromString(String value) {
        String[] params = value.split(",");
        return new Task(Integer.parseInt(value.split(",")[0]), params[2], params[4], Status.valueOf(params[3]));
    }

    public static Epic epicFromString(String value) {
        String[] params = value.split(",");
        return new Epic(Integer.parseInt(value.split(",")[0]), params[2], params[4]);
    }

    public static int getId(String value) {
        return Integer.parseInt(value.split(",")[0]);
    }

    public static String getTaskType(String line) { //возвращает String , тк первая строка заголовок, там нет тасктайпа
        return line.split(",")[1];
    }
}
