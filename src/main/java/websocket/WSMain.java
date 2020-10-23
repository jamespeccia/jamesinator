package websocket;

import org.json.JSONObject;

public class WSMain {

    //protected final String BYBIT_WS_ADDRESS = "wss://stream-testnet.bybit.com/realtime";
    protected final String BYBIT_WS_ADDRESS = "wss://stream.bybit.com/realtime";
    protected final String PHEMEX_WS_ADDRESS = "wss://phemex.com/ws";

    public WSMain(final JSONObject CURRENT_STATE) {

        BybitWSClient bybitClient = new BybitWSClient(BYBIT_WS_ADDRESS, CURRENT_STATE);
        bybitClient.connect();
        PhemexWSClient phemexClient = new PhemexWSClient(PHEMEX_WS_ADDRESS, CURRENT_STATE);
        phemexClient.connect();

    }
}
