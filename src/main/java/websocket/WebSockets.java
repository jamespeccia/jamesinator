package websocket;

import org.json.JSONObject;


public class WebSockets {

    protected final String BYBIT_WS_ADDRESS = "wss://stream.bybit.com/realtime";
    //protected final String BYBIT_WS_ADDRESS = "wss://stream-testnet.bybit.com/realtime";
    protected final String PHEMEX_WS_ADDRESS = "wss://phemex.com/ws";

    public WebSockets(JSONObject currentState) {

        BybitWebSocketClient bybitClient = new BybitWebSocketClient(BYBIT_WS_ADDRESS, currentState);
        bybitClient.connect();
        bybitClient.subscribe();

        PhemexWebSocketClient phemexClient = new PhemexWebSocketClient(PHEMEX_WS_ADDRESS, currentState);
        phemexClient.connect();
        phemexClient.subscribe();

    }
}
