import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Subtask> subTasks;


    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
        this.subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, int id, ArrayList<Subtask> subTasks) {
        super(name, description, status, id);
        this.subTasks = subTasks;
    }

    void setStatus(Status statusSubTask){
        Status summaryStatus = Status.NEW;
        if (statusSubTask.equals(Status.IN_PROGRESS)){
            summaryStatus = Status.IN_PROGRESS;
        }else {
            for (Subtask subTask : subTasks) {
                if(!statusSubTask.equals(subTask.status)){
                    summaryStatus = Status.IN_PROGRESS;
                    break;
                } else {
                    summaryStatus = statusSubTask;
                }
            }
        }
        this.status = summaryStatus;
   }

    public void setSubTask(Subtask subTask) {
        subTasks.add(subTask);
        setStatus(subTask.status);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}
