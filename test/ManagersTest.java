import manager.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ManagersTest {

    @Test
    void getDefaultReturnActualObject() {
        TaskManager actual = Managers.getDefault();
        Assertions.assertNotNull(actual);
        Assertions.assertInstanceOf(InMemoryTaskManager.class, actual);
    }

    @Test
    void getDefaultHistoryReturnActualObject() {
        HistoryManager actual = Managers.getDefaultHistory();
        Assertions.assertNotNull(actual);
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, actual);
    }
}