package websocket;

import logger.Logger;
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

public abstract class WSClient implements WSInterface {

    protected final WebSocketContainer CONTAINER;
    protected final String EXCHANGE;
    protected final String ADDRESS;
    protected final JSONObject CURRENT_STATE;

    protected Session session;
    protected Timer timer;
    protected long lastMessageTime;

    protected WSClient(final String EXCHANGE, final String ADDRESS, final JSONObject CURRENT_STATE) {
        this.CONTAINER = ContainerProvider.getWebSocketContainer();
        this.EXCHANGE = EXCHANGE;
        this.ADDRESS = ADDRESS;
        this.CURRENT_STATE = CURRENT_STATE;
    }

    public void connect() {
        boolean connected = false;
        while (!connected) {
            try {
                session = this.CONTAINER.connectToServer(this, new URI(this.ADDRESS));
                connected = true;
                //this.startHeartbeatService(3000);
                this.authenticate();
                this.subscribe();
            } catch (DeploymentException | URISyntaxException | IOException e) {
                Logger.log("Unable to connect to " + EXCHANGE + " WebSocket. Attempting to reconnect in 5000ms.", e.toString());
                connected = false;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Logger.log("Thread was interrupted while waiting to reconnect to " + EXCHANGE + " WebSocket.", ie.toString());
                }
            }
        }
    }

    public void send(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    @OnOpen
    public void onOpen(Session session) {
        Logger.log("Successfully connected to " + EXCHANGE + " WebSocket.");
        this.lastMessageTime = System.currentTimeMillis();
    }

    @OnMessage
    public abstract void onMessage(Session session, String msg);

    @OnError
    public void onError(Session session, Throwable throwable) {
        Logger.log("Error occurred in " + EXCHANGE + " WebSocket.", throwable.toString());
        try {
            session.close();
        } catch (IOException e) {
            Logger.log("Error closing " + EXCHANGE +" WebSocket.", e.toString());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        Logger.log(EXCHANGE + " WebSocket closed.", closeReason.toString());
        this.connect();
    }

    public abstract void startHeartbeatService(final int RATE);

    public abstract void authenticate();

    public abstract void subscribe();

    public String sign(String parameters, String SECRET) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(parameters.getBytes()));
    }

}
