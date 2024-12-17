package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyTasks = new ArrayList<>();

    @Override
    public List<Task> getHistory() {

        return historyTasks;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            //при изменении задачи в истории остается версия задачи на момент ее просмотра
            Task newTask = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus());
            historyTasks.add(newTask);
            if (historyTasks.size() > 10) {
                historyTasks.removeFirst();
            }
        }
    }
}
