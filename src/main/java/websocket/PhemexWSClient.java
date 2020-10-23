package websocket;

import api.PhemexClient;
import logger.Logger;
import org.json.JSONObject;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;

@ClientEndpoint
public class PhemexWSClient extends WSClient {

    public PhemexWSClient(final String ADDRESS, JSONObject CURRENT_STATE) {
        super("Phemex", ADDRESS, CURRENT_STATE);
    }

    @OnMessage @Override
    public void onMessage(Session session, String msg) {
        final JSONObject MESSAGE = new JSONObject(msg);

        super.lastMessageTime = System.currentTimeMillis();

        try {
            super.CURRENT_STATE
                    .getJSONObject("lastUpdated")
                    .put("phemex", super.lastMessageTime);
        } catch (Exception ignored) {
        }

        try {
            if (MESSAGE.getJSONObject("market24h").get("symbol").equals("BTCUSD")) {
                this.CURRENT_STATE
                        .getJSONObject("prices").getJSONObject("BTCUSD")
                        .put("phemex", MESSAGE.getJSONObject("market24h").getDouble("close") / 10000.0);
                this.CURRENT_STATE
                        .getJSONObject("lastUpdated").getJSONObject("BTCUSD")
                        .put("phemex", super.lastMessageTime);

                return;
            }
        } catch (Exception ignored) {
        }

        try {
            if (MESSAGE.getJSONObject("market24h").get("symbol").equals("ETHUSD")) {
                this.CURRENT_STATE
                        .getJSONObject("prices").getJSONObject("ETHUSD")
                        .put("phemex", MESSAGE.getJSONObject("market24h").getDouble("close") / 10000.0);
                this.CURRENT_STATE
                        .getJSONObject("lastUpdated").getJSONObject("ETHUSD")
                        .put("phemex", lastMessageTime);
                return;
            }
        } catch (Exception ignored) {
        }

        try {
            if (MESSAGE.getJSONObject("market24h").get("symbol").equals("XRPUSD")) {
                this.CURRENT_STATE
                        .getJSONObject("prices").getJSONObject("XRPUSD")
                        .put("phemex", MESSAGE.getJSONObject("market24h").getDouble("close") / 10000.0);
                this.CURRENT_STATE
                        .getJSONObject("lastUpdated").getJSONObject("XRPUSD")
                        .put("phemex", lastMessageTime);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void startHeartbeatService(final int RATE) {
        final String HEARTBEAT = "{\"method\":\"server.ping\",\"params\":[],\"id\":0}";
        if(timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new HeartbeatService(this, HEARTBEAT, RATE), 0, RATE);
    }

    @Override
    public void subscribe() {
        final String PRICE_SUBSCRIPTION = "{\"method\":\"market24h.subscribe\",\"params\":[],\"id\":0}";
        final String POSITION_SUBSCRIPTION = "{\"method\":\"aop.subscribe\",\"params\":[],\"id\":0}";
        try {
            this.send(PRICE_SUBSCRIPTION);
            this.send(POSITION_SUBSCRIPTION);
        } catch (IOException e) {
            Logger.log("Phemex price subscription interrupted.", e.toString());
        }
    }

    @Override
    public void authenticate() {
        long expiry = System.currentTimeMillis() * 1000;

        String signature = null;
        try {
            signature = sign(PhemexClient.API_KEY + expiry, PhemexClient.SECRET);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Logger.log("Error signing Phemex WebSocket authentication request. Authentication failed.", e.toString());
        }

        final String AUTH_SUBSCRIPTION = "{\"method\":\"user.auth\",\"params\":[\"API\",\"" + PhemexClient.API_KEY + "\",\"" + signature + "\"," + expiry + "],\"id\": 0}";
        try {
            send(AUTH_SUBSCRIPTION);
        } catch (IOException e) {
            Logger.log("Phemex authentication interrupted.", e.toString());
        }
    }
}
