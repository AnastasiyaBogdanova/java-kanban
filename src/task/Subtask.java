package task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s\n", id, TaskType.SUBTASK, name, status, description, epicId);
    }

    public static Subtask fromString(String value) {
        String[] params = value.split(",");
        return new Subtask(
                Integer.parseInt(params[0]),
                params[2],
                params[4],
                Status.valueOf(params[3]),
                Integer.parseInt(params[5])
        );
    }

}
