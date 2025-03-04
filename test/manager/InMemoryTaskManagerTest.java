package manager;

import manager.InMemoryTaskManager;
import manager.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    public InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}