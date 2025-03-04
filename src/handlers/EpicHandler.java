package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.InvalidTaskStartTimeException;
import manager.TaskManager;
import server.HttpTaskServer;
import task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager taskManager) {
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
        } catch (NoSuchElementException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (InvalidTaskStartTimeException exception) {
            sendHasInteractions(httpExchange);
        }
    }

    private boolean isSubtaskEndpoint(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        if (path.split("/").length == 4 && path.split("/")[3].equals("subtasks")) {
            return true;
        }
        return false;
    }

    private void delete(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(httpExchange);
        if (taskIdOpt.isPresent()) {
            taskManager.removeEpicById(taskIdOpt.get());
            sendText(httpExchange, "Эпик " + taskIdOpt.get() + " успешно удален");
        }
    }

    private void post(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String task = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic newTask = HttpTaskServer.getGson().fromJson(task, Epic.class);
        Optional<Integer> idFromJson = getIdFromJson(task);

        if (idFromJson.isPresent()) {
            taskManager.updateEpic(newTask);
            sendText(httpExchange, "Эпик обновлен");
        } else {
            taskManager.addNewEpic(newTask);
            sendText(httpExchange, "Эпик успешно создан");
        }
    }

    private void get(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(httpExchange);
        String response;
        if (taskIdOpt.isPresent() && !isSubtaskEndpoint(httpExchange)) {
            response = HttpTaskServer.getGson().toJson(taskManager.getEpicById(taskIdOpt.get()).get());
        } else if (taskIdOpt.isPresent() && isSubtaskEndpoint(httpExchange)) {
            response = HttpTaskServer.getGson().toJson(taskManager.getSubtasksByEpic(taskIdOpt.get()));
        } else {
            response = HttpTaskServer.getGson().toJson(taskManager.getAllEpics());
        }
        sendText(httpExchange, response);
    }

}
