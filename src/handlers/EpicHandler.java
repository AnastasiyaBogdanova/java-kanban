package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.InvalidTaskStartTimeException;
import server.HttpTaskServer;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
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
                Epic newTask = gson.fromJson(task, Epic.class);
                Optional<Integer> idFromJson = getIdFromJson(task);
                try {
                    if (idFromJson.isPresent()) {
                        taskManager.updateEpic(newTask);
                        sendText(httpExchange, "Эпик обновлен");
                    } else {
                        taskManager.addNewEpic(newTask);
                        sendText(httpExchange, "Эпик успешно создан");
                    }
                } catch (InvalidTaskStartTimeException exception) {
                    sendHasInteractions(httpExchange);
                } catch (NoSuchElementException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;

            case "GET":
                try {
                    if (taskIdOpt.isPresent() && !isSubtaskEndpoint(httpExchange)) {
                        response = gson.toJson(taskManager.getEpicById(taskIdOpt.get()).get());
                    } else if (taskIdOpt.isPresent() && isSubtaskEndpoint(httpExchange)) {
                        response = gson.toJson(taskManager.getSubtasksByEpic(taskIdOpt.get()));
                    } else {
                        response = gson.toJson(taskManager.getAllEpics());
                    }
                    sendText(httpExchange, response);
                } catch (NoSuchElementException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    if (taskIdOpt.isPresent()) {
                        taskManager.removeEpicById(taskIdOpt.get());
                        response = "Эпик " + taskIdOpt.get() + " успешно удален";
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
