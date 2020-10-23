package webserver;

import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import webserver.routes.Index;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {

    private final HttpServer HTTP_SERVER;

    public WebServer(final int PORT, final JSONObject CURRENT_STATE) throws IOException {
        this.HTTP_SERVER = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.HTTP_SERVER.createContext("/", new Index(CURRENT_STATE));
        this.HTTP_SERVER.setExecutor(null);
    }

    public void start() {
        this.HTTP_SERVER.start();
    }
}
