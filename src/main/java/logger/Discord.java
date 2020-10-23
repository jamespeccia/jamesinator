package logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

public class Discord {
    private static URI WEBHOOK_URI;

    static {
        try {
            WEBHOOK_URI =
                    new URI(
                            "https://discordapp.com/api/webhooks/618928460043386903/PVwZh9kJP2X0q0kDelTt2K8xEmYN2fObo83PJsl0GeGdUSlW7296GxJBEgjY1W-_51vi");
        } catch (URISyntaxException ignored) {
        }
    }

    public static void send(String message) {
        JSONObject body = new JSONObject();
        body.put("content", message);
        body.put("username", "Jamesinator");

        HttpPost httpPost = new HttpPost(WEBHOOK_URI);
        httpPost.setEntity(new StringEntity(body.toString(), "UTF-8"));
        httpPost.setHeader("Content-Type", "application/json");
        HttpClient httpClient = HttpClients.createMinimal();

        try {
            httpClient.execute(httpPost);
        } catch (IOException e) {
            System.out.println("Unable to POST to Discord.");
        }
    }
}
