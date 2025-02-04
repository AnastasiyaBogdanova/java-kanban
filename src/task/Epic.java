package task;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTasksId = new ArrayList<>();

    public ArrayList<Integer> getSubTasksId() {
        return subTasksId;
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {

        super(id, name, description, Status.NEW);
    }

    public void removeSubTask(int subTaskId) {
        subTasksId.remove(Integer.valueOf(subTaskId));
    }

    public void setSubTasksId(ArrayList<Integer> subTasksId) {
        this.subTasksId = subTasksId;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s\n", id, TaskType.EPIC, name, status, description);
    }

    public static Epic fromString(String value) {
        String[] params = value.split(",");
        return new Epic(Integer.parseInt(params[0]), params[2], params[4]);
    }

}
