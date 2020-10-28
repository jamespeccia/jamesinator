package webserver.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class UpdateBounds implements HttpHandler {

    private final JSONObject CURRENT_STATE;

    public UpdateBounds(final JSONObject CURRENT_STATE) {
        this.CURRENT_STATE = CURRENT_STATE;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream body = exchange.getRequestBody();
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String value = br.readLine();
        System.out.println(value);
        String response = CURRENT_STATE.toString();
        exchange.getResponseHeaders().set("Content-Type", "application/json;");
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}