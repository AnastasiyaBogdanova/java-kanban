package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTasksId = new ArrayList<>();

    public ArrayList<Integer> getSubTasksId() {
        return subTasksId;
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW, Duration.ofMinutes(0), LocalDateTime.now());
    }

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
        startTime = LocalDateTime.now();
        duration = Duration.ofMinutes(0);
    }

    public Epic(Integer id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    public void removeSubTask(int subTaskId) {
        subTasksId.remove(Integer.valueOf(subTaskId));
    }

    public void setSubTasksId(ArrayList<Integer> subTasksId) {
        this.subTasksId = subTasksId;
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                "subTasksId=" + subTasksId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", duration=" + duration.toMinutes() + '\'' +
                ", startTime=" + startTime + '\'' +
                ", endTime=" + endTime +
                '}';
    }

}
