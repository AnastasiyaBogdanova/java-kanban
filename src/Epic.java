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
        subTasksId.remove(subTaskId);
    }

    public void setSubTasksId(ArrayList<Integer> subTasksId) {
        this.subTasksId = subTasksId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksId=" + subTasksId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
