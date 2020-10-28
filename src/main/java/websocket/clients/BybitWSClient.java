package websocket;

import api.Client;
import logger.Logger;
import org.json.JSONObject;
import state.State;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;

@ClientEndpoint
public class BybitWSClient extends WSClient {

    public BybitWSClient(final String ADDRESS, final State CURRENT_STATE) {
        super(State.BYBIT, ADDRESS, CURRENT_STATE);
    }

    @OnMessage
    @Override
    public void onMessage(Session session, String msg) {
        final JSONObject MESSAGE = new JSONObject(msg);
        CURRENT_STATE.setLastPingTimeNow(State.BYBIT);
        try {
            JSONObject data = (JSONObject) MESSAGE.getJSONArray("data").get(0);
            try {
                if (data.get("symbol").equals("BTCUSD")) {
                    this.CURRENT_STATE.setPrice(State.BTCUSD, State.BYBIT, data.getDouble("price"));
                    this.CURRENT_STATE.setLastUpdatedNow(State.BTCUSD, State.BYBIT);
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (data.get("symbol").equals("ETHUSD")) {
                    this.CURRENT_STATE.setPrice(State.ETHUSD, State.BYBIT, data.getDouble("price"));
                    this.CURRENT_STATE.setLastUpdatedNow(State.ETHUSD, State.BYBIT);
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (data.get("symbol").equals("XRPUSD")) {
                    this.CURRENT_STATE.setPrice(State.XRPUSD, State.BYBIT, data.getDouble("price"));
                    this.CURRENT_STATE.setLastUpdatedNow(State.XRPUSD, State.BYBIT);
                    return;
                }
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) { // Not a price update
        }

        try {
            if (MESSAGE.getString("topic").equals("position")) {
                JSONObject data = (JSONObject) MESSAGE.getJSONArray("data").get(0);
                //this.CURRENT_STATE.setTradeStatus(data.getString("symbol"), State.BYBIT, "New");
                this.CURRENT_STATE.setTradeQuantity(data.getString("symbol"), State.BYBIT, data.getInt("size"));
                this.CURRENT_STATE.setTradeSide(data.getString("symbol"), State.BYBIT, data.getString("side"));
            }
        } catch (Exception ignored) {
        }

    }

    @Override
    public void startHeartbeatService(final int RATE) {
        final String HEARTBEAT = "{\"op\":\"ping\"}";
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new HeartbeatService(this, HEARTBEAT, RATE), 0, RATE);
    }

    public void authenticate() {
        long expiry = System.currentTimeMillis() + 1000;

        String signature = null;
        try {
            signature = sign("GET/realtime" + expiry, Client.BYBIT_API_SECRET);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Logger.log("Error signing Bybit's WebSocket authentication request. Authentication failed.", e.toString());
            return;
        }

        final String AUTH = "{\"op\":\"auth\",\"args\":[\"" + Client.BYBIT_API_KEY + "\"," + expiry + ",\"" + signature + "\"]}";
        try {
            send(AUTH);
        } catch (IOException e) {
            Logger.log("Bybit WebSocket authentication interrupted.", e.toString());
        }
    }

    @Override
    public void subscribe() {
        final String PRICE_SUBSCRIPTION = "{\"op\": \"subscribe\", \"args\": [\"trade.BTCUSD\"]}";
        try {
            send(PRICE_SUBSCRIPTION);
        } catch (IOException e) {
            Logger.log("Bybit WebSocket price subscription interrupted.", e.toString());
        }

        final String POSITION_SUBSCRIPTION = "{\"op\":\"subscribe\",\"args\":[\"position\"]}";
        try {
            send(POSITION_SUBSCRIPTION);
        } catch (IOException e) {
            Logger.log("Bybit WebSocket position subscription interrupted.", e.toString());
        }
    }
}
