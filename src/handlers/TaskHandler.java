package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.InvalidTaskStartTimeException;
import manager.TaskManager;
import server.HttpTaskServer;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        try {
            switch (method) {
                case "POST":
                    post(httpExchange);
                    break;
                case "GET":
                    get(httpExchange);
                    break;
                case "DELETE":
                    delete(httpExchange);
                    break;
                default:
                    sendText(httpExchange, "Такой метод не предусмотрен!");
            }
        } catch (InvalidTaskStartTimeException exception) {
            sendHasInteractions(httpExchange);
        } catch (NoSuchElementException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void post(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String task = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task newTask = HttpTaskServer.getGson().fromJson(task, Task.class);
        Optional<Integer> idFromJson = getIdFromJson(task);
        if (idFromJson.isPresent()) {
            taskManager.updateTask(newTask);
            sendText(httpExchange, "Таск обновлен");
        } else {
            taskManager.addNewTask(newTask);
            sendText(httpExchange, "Таск успешно создан");
        }
    }

    private void get(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(httpExchange);
        String response;
        if (taskIdOpt.isPresent()) {
            response = HttpTaskServer.getGson().toJson(taskManager.getTaskById(taskIdOpt.get()).get());
        } else {
            response = HttpTaskServer.getGson().toJson(taskManager.getAllTasks());
        }
        sendText(httpExchange, response);
    }

    private void delete(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(httpExchange);
        if (taskIdOpt.isPresent()) {
            taskManager.removeTaskById(taskIdOpt.get());
            sendText(httpExchange, "Таск id = " + taskIdOpt.get() + " успешно удален");
        }
    }

}
