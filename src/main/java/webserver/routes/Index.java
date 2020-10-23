package webserver.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class Index implements HttpHandler {

    private final JSONObject CURRENT_STATE;

    public Index(final JSONObject CURRENT_STATE) {
        this.CURRENT_STATE = CURRENT_STATE;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = CURRENT_STATE.toString();
        exchange.getResponseHeaders().set("Content-Type", "application/json;");
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
