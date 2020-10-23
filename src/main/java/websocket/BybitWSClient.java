package websocket;

import api.BybitClient;
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
public class BybitWSClient extends WSClient {

    public BybitWSClient(final String ADDRESS, final JSONObject CURRENT_STATE) {
        super("Bybit", ADDRESS, CURRENT_STATE);
    }

    @OnMessage @Override
    public void onMessage(Session session, String msg) {
        final JSONObject MESSAGE = new JSONObject(msg);

        super.lastMessageTime = System.currentTimeMillis();

        try {
            super.CURRENT_STATE
                    .getJSONObject("lastUpdated")
                    .put("bybit", super.lastMessageTime);
        } catch (Exception ignored) {
        }

        try {
            JSONObject data = (JSONObject) MESSAGE.getJSONArray("data").get(0);
            if (data.get("symbol").equals("BTCUSD")) {
                this.CURRENT_STATE
                        .getJSONObject("prices").getJSONObject("BTCUSD")
                        .put("bybit", data.getDouble("last_price"));
                this.CURRENT_STATE
                        .getJSONObject("lastUpdated").getJSONObject("BTCUSD")
                        .put("bybit", super.lastMessageTime);
                return;
            }

        } catch (Exception ignored) {
        }

        try {
            JSONObject data = (JSONObject) MESSAGE.getJSONArray("data").get(0);
            if (data.get("symbol").equals("ETHUSD")) {
                this.CURRENT_STATE
                        .getJSONObject("prices").getJSONObject("ETHUSD")
                        .put("bybit", data.getDouble("last_price"));
                this.CURRENT_STATE
                        .getJSONObject("lastUpdated").getJSONObject("ETHUSD")
                        .put("bybit", super.lastMessageTime);
                return;
            }
        } catch (Exception ignored) {
        }

        try {
            JSONObject data = (JSONObject) MESSAGE.getJSONArray("data").get(0);
            if (data.get("symbol").equals("XRPUSD")) {
                this.CURRENT_STATE
                        .getJSONObject("prices").getJSONObject("XRPUSD")
                        .put("bybit", data.getDouble("last_price"));
                this.CURRENT_STATE
                        .getJSONObject("lastUpdated").getJSONObject("XRPUSD")
                        .put("bybit", super.lastMessageTime);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void startHeartbeatService(final int RATE) {
        final String HEARTBEAT = "{\"op\":\"ping\"}";
        if(timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new HeartbeatService(this, HEARTBEAT, RATE), 0, RATE);
    }

    @Override
    public void authenticate() {
        long expiry = System.currentTimeMillis() + 1000;

        String signature = null;
        try {
            signature = sign("GET/realtime" + expiry, BybitClient.SECRET);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Logger.log("Error signing Bybit's WebSocket authentication request. Authentication failed.", e.toString());
            return;
        }

        final String AUTH = "{\"op\":\"auth\",\"args\":[\"" + BybitClient.API_KEY + "\"," + expiry + ",\"" + signature + "\"]}";
        try {
            send(AUTH);
        } catch (IOException e) {
            Logger.log("Bybit authentication interrupted.", e.toString());
        }
    }

    @Override
    public void subscribe() {
        try {
            String PRICE_SUBSCRIPTION = "{\"op\":\"subscribe\",\"args\":[\"instrument.BTCUSD|ETHUSD|XRPUSD\"]}";
            send(PRICE_SUBSCRIPTION);
            String POSITION_SUBSCRIPTION = "{\"op\":\"subscribe\",\"args\":[\"position\"]}";
            send(POSITION_SUBSCRIPTION);
        } catch (IOException e) {
            Logger.log("Bybit price and/or position subscription interrupted.", e.toString());
        }
    }
}
