import manager.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    public InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}