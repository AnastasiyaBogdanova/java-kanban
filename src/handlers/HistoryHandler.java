package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.HttpTaskServer;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET":
                sendText(httpExchange, HttpTaskServer.getGson().toJson(HttpTaskServer.getManager().getHistory()));
                break;
            default:
                sendText(httpExchange, "Такой метод не предусмотрен!");
        }
    }

}
