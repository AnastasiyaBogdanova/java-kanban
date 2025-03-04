package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.InvalidTaskStartTimeException;
import server.HttpTaskServer;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String response;

        Optional<Integer> taskIdOpt = getTaskId(httpExchange);
        Gson gson = HttpTaskServer.getGson();
        TaskManager taskManager = HttpTaskServer.getManager();
        switch (method) {
            case "POST":
                InputStream inputStream = httpExchange.getRequestBody();
                String task = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task newTask = gson.fromJson(task, Task.class);
                Optional<Integer> idFromJson = getIdFromJson(task);
                try {
                    if (idFromJson.isPresent()) {
                        taskManager.updateTask(newTask);
                        sendText(httpExchange, "Таск обновлен");
                    } else {
                        taskManager.addNewTask(newTask);
                        sendText(httpExchange, "Таск успешно создан");
                    }
                } catch (InvalidTaskStartTimeException exception) {
                    sendHasInteractions(httpExchange);
                } catch (NoSuchElementException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;

            case "GET":
                try {
                    if (taskIdOpt.isPresent()) {
                        response = gson.toJson(taskManager.getTaskById(taskIdOpt.get()).get());
                    } else {
                        response = gson.toJson(taskManager.getAllTasks());
                    }
                    sendText(httpExchange, response);
                } catch (NoSuchElementException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    if (taskIdOpt.isPresent()) {
                        taskManager.removeTaskById(taskIdOpt.get());
                        response = "Таск id = " + taskIdOpt.get() + " успешно удален";
                        sendText(httpExchange, response);
                    }
                } catch (NoSuchElementException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            default:
                sendText(httpExchange, "Такой метод не предусмотрен!");
        }
    }

}
