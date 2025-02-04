package task;

import java.util.Objects;

public class Task {

    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setStatus(Status status) {

        this.status = status;
    }

    public Status getStatus() {

        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s\n", id, TaskType.TASK, name, status, description);
    }

    public static Task fromString(String value) {
        String[] params = value.split(",");
        return new Task(Integer.parseInt(params[0]), params[2], params[4], Status.valueOf(params[3]));
    }

}
