package file;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CsvConverter {
    static final String title = "id,type,name,status,description,epic,StartTime,EndTime,Duration\n";

    public static String getTitle() {
        return title;
    }

    public static String toCSV(Subtask subtask) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                subtask.getId(),
                TaskType.SUBTASK,
                subtask.getName(),
                subtask.getStatus(),
                subtask.getDescription(),
                subtask.getEpicId(),
                subtask.getStartTime(),
                subtask.getEndTime(),
                subtask.getDuration().toMinutes()
        );
    }

    public static String toCSV(Task task) {
        return String.format("%s,%s,%s,%s,%s,,%s,%s,%s\n",
                task.getId(),
                TaskType.TASK,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                task.getStartTime(),
                task.getEndTime(),
                task.getDuration().toMinutes()
        );
    }

    public static String toCSV(Epic epic) {
        return String.format("%s,%s,%s,%s,%s,,%s,%s,%s\n",
                epic.getId(),
                TaskType.EPIC,
                epic.getName(),
                epic.getStatus(),
                epic.getDescription(),
                epic.getStartTime(),
                epic.getEndTime(),
                epic.getDuration().toMinutes()
        );
    }


    public static Subtask subtaskFromString(String value) {
        String[] params = value.split(",");
        return new Subtask(
                Integer.parseInt(params[0]),
                params[2],
                params[4],
                Status.valueOf(params[3]),
                Integer.parseInt(params[5]),
                Duration.ofMinutes(Long.parseLong(params[8])),
                LocalDateTime.parse(params[6])

        );

    }

    public static Task taskFromString(String value) {
        String[] params = value.split(",");
        return new Task(Integer.parseInt(params[0]),
                params[2],
                params[4],
                Status.valueOf(params[3]),
                Duration.ofMinutes(Long.parseLong(params[8])),
                LocalDateTime.parse(params[6])
        );
    }

    public static Epic epicFromString(String value) {
        String[] params = value.split(",");
        return new Epic(Integer.parseInt(params[0]),
                params[2],
                params[4],
                Status.valueOf(params[3]),
                Duration.ofMinutes(Long.parseLong(params[8])),
                LocalDateTime.parse(params[6])
        );
    }

    public static int getId(String value) {
        return Integer.parseInt(value.split(",")[0]);
    }

    public static String getTaskType(String line) { //возвращает String , тк первая строка заголовок, там нет тасктайпа
        return line.split(",")[1];
    }
}
