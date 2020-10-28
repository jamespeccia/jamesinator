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
public class PhemexWSClient extends WSClient {

    public PhemexWSClient(final String ADDRESS, State CURRENT_STATE) {
        super(State.PHEMEX, ADDRESS, CURRENT_STATE);
    }

    @OnMessage
    @Override
    public void onMessage(Session session, String msg) {
        final JSONObject MESSAGE = new JSONObject(msg);
        CURRENT_STATE.setLastPingTimeNow(State.PHEMEX);
//System.out.println(msg);
        try {
            final JSONObject data = MESSAGE.getJSONObject("market24h");

            try {
                if (data.get("symbol").equals("BTCUSD")) {
                    this.CURRENT_STATE.setPrice(State.BTCUSD, State.PHEMEX, data.getDouble("close") / 10000.0);
                    this.CURRENT_STATE.setLastUpdatedNow(State.BTCUSD, State.PHEMEX);
                    return;
                }
            } catch (Exception ignored) {
            }

            try {
                if (data.get("symbol").equals("ETHUSD")) {
                    this.CURRENT_STATE.setPrice(State.ETHUSD, State.PHEMEX, data.getDouble("close") / 10000.0);
                    this.CURRENT_STATE.setLastUpdatedNow(State.ETHUSD, State.PHEMEX);
                    return;
                }
            } catch (Exception ignored) {
            }

            try {
                if (data.get("symbol").equals("XRPUSD")) {
                    this.CURRENT_STATE.setPrice(State.XRPUSD, State.PHEMEX, data.getDouble("close") / 10000.0);
                    this.CURRENT_STATE.setLastUpdatedNow(State.XRPUSD, State.PHEMEX);
                    return;
                }
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }

        try {
            JSONObject data = (JSONObject) MESSAGE.getJSONArray("positions").get(0);
            //this.CURRENT_STATE.setTradeStatus(data.getString("symbol"), State.PHEMEX, "New");
            this.CURRENT_STATE.setTradeQuantity(data.getString("symbol"), State.PHEMEX, data.getInt("size"));
            this.CURRENT_STATE.setTradeSide(data.getString("symbol"), State.PHEMEX, data.getString("side"));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void startHeartbeatService(final int RATE) {
        final String HEARTBEAT = "{\"method\":\"server.ping\",\"params\":[],\"id\":0}";
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new HeartbeatService(this, HEARTBEAT, RATE), 0, RATE);
    }

    @Override
    public void subscribe() {
        final String PRICE_SUBSCRIPTION = "{\"method\":\"market24h.subscribe\",\"params\":[],\"id\":0}";
        try {
            this.send(PRICE_SUBSCRIPTION);
        } catch (IOException e) {
            Logger.log("Phemex WebSocket price subscription interrupted.", e.toString());
        }

        final String POSITION_SUBSCRIPTION = "{\"method\":\"aop.subscribe\",\"params\":[],\"id\":2}";
        try {
            this.send(POSITION_SUBSCRIPTION);
        } catch (IOException e) {
            Logger.log("Phemex WebSocket price subscription interrupted.", e.toString());
        }
    }

    @Override
    public void authenticate() {
        long expiry = System.currentTimeMillis() / 1000 + 5;

        String signature = null;
        try {
            signature = sign(Client.PHEMEX_API_KEY + expiry, Client.PHEMEX_API_SECRET);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Logger.log("Error signing Phemex WebSocket authentication request. Authentication failed.", e.toString());
        }

        final String AUTH_SUBSCRIPTION = "{\"method\": \"user.auth\", \"params\": [\"API\", \"" +
                Client.PHEMEX_API_KEY + "\", \"" + signature + "\", " + expiry + "], \"id\": 1234}";
        try {
            send(AUTH_SUBSCRIPTION);
        } catch (IOException e) {
            Logger.log("Phemex WebSocket authentication interrupted.", e.toString());
        }
    }
}
