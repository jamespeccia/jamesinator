package websocket;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

@ClientEndpoint
public class BybitWebSocketClient {

    protected WebSocketContainer container;
    protected Session userSession;
    protected String address;
    protected JSONObject currentState;


    public BybitWebSocketClient(String address, JSONObject currentState) {
        this.container = ContainerProvider.getWebSocketContainer();
        this.address = address;
        this.currentState = currentState;
    }

    private static String sign(String parameters, String SECRET) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(parameters.getBytes()));
    }

    public void connect() {
        try {
            userSession = container.connectToServer(this, new URI(this.address));
            this.startHeartbeat();
        } catch (DeploymentException | URISyntaxException | IOException e) {
            e.printStackTrace();
        }

    }

    public void send(String message) throws IOException {
        userSession.getBasicRemote().sendText(message);
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Successfully connected to Bybit's WebSocket.");
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Disconnected from Bybit's WebSocket. Attempting to reconnect.");
        this.connect();
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        JSONObject messageJSON = new JSONObject(message);
        try {
            messageJSON = (JSONObject) messageJSON.getJSONArray("data").get(0);
            if (messageJSON.get("symbol").equals("BTCUSD")) {
                this.currentState.getJSONObject("prices").put(Constants.BYBIT_BTC_PRICE, messageJSON.getDouble("last_price"));
                this.currentState.getJSONObject("prices").put(Constants.DIFFERENCE_BTC_PRICE, currentState.getJSONObject("prices").getDouble(Constants.BYBIT_BTC_PRICE) - currentState.getJSONObject("prices").getDouble(Constants.PHEMEX_BTC_PRICE));
            }

        } catch (Exception ignored) {
        }
    }

    public void disconnect() throws IOException {
        userSession.close();
    }

    private void startHeartbeat() {
        JSONObject heartbeatJSON = new JSONObject();
        heartbeatJSON.put("op", "ping");

        String heartbeat = heartbeatJSON.toString();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    send(heartbeat);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, 0, 5000);
    }

    public void subscribe() {
        JSONObject subscriptionJSON = new JSONObject();
        subscriptionJSON.put("op", "subscribe");
        subscriptionJSON.put("args", new String[]{"instrument.BTCUSD"});

        String subscription = subscriptionJSON.toString();

        try {
            send(subscription);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
