package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.InvalidTaskStartTimeException;
import server.HttpTaskServer;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
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
                Subtask newTask = gson.fromJson(task, Subtask.class);
                Optional<Integer> idFromJson = getIdFromJson(task);
                try {
                    if (idFromJson.isPresent()) {
                        taskManager.updateSubTask(newTask);
                        sendText(httpExchange, "Сабтаск обновлен");
                    } else {
                        taskManager.addNewSubTask(newTask);
                        sendText(httpExchange, "Сабтаск успешно создан");
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
                        response = gson.toJson(taskManager.getSubTaskById(taskIdOpt.get()).get());
                    } else {
                        response = gson.toJson(taskManager.getAllSubTasks());
                    }
                    sendText(httpExchange, response);
                } catch (NoSuchElementException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    if (taskIdOpt.isPresent()) {
                        taskManager.removeSubTaskById(taskIdOpt.get());
                        response = "Сабтаск id = " + taskIdOpt.get() + " успешно удален";
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

