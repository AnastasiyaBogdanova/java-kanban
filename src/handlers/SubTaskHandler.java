package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.InvalidTaskStartTimeException;
import manager.TaskManager;
import server.HttpTaskServer;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubTaskHandler(TaskManager taskManager) {
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

    private void get(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(httpExchange);
        String response;
        if (taskIdOpt.isPresent()) {
            response = HttpTaskServer.getGson().toJson(taskManager.getSubTaskById(taskIdOpt.get()).get());
        } else {
            response = HttpTaskServer.getGson().toJson(taskManager.getAllSubTasks());
        }
        sendText(httpExchange, response);
    }

    private void delete(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(httpExchange);
        if (taskIdOpt.isPresent()) {
            taskManager.removeSubTaskById(taskIdOpt.get());
            sendText(httpExchange, "Сабтаск id = " + taskIdOpt.get() + " успешно удален");
        }
    }

    private void post(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String task = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask newTask = HttpTaskServer.getGson().fromJson(task, Subtask.class);
        Optional<Integer> idFromJson = getIdFromJson(task);
        if (idFromJson.isPresent()) {
            taskManager.updateSubTask(newTask);
            sendText(httpExchange, "Сабтаск обновлен");
        } else {
            taskManager.addNewSubTask(newTask);
            sendText(httpExchange, "Сабтаск успешно создан");
        }
    }
}

