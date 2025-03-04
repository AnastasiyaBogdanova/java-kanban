package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.HttpTaskServer;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET":
                sendText(httpExchange, HttpTaskServer.getGson().toJson(taskManager.getHistory()));
                break;
            default:
                sendText(httpExchange, "Такой метод не предусмотрен!");
        }
    }

}
