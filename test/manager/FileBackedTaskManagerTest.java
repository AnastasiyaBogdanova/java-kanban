package manager;

import manager.FileBackedTaskManager;
import manager.TaskManagerTest;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    FileBackedTaskManager fileManager;

    @Override
    public FileBackedTaskManager getTaskManager() {

        try {
            fileManager = FileBackedTaskManager.loadFromFile(File.createTempFile("file", "_1"));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return fileManager;
    }
}
