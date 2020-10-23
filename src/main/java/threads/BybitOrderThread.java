package threads;

import org.json.JSONObject;

import api.BybitClient;

public class BybitOrderThread extends Thread {

    private final JSONObject currentState;
    private final boolean buy;
    private final int quantity;

    public BybitOrderThread(JSONObject currentState, boolean buy, int quantity) {
        this.currentState = currentState;
        this.buy = buy;
        this.quantity = quantity;
    }

    @Override
    public void run() {
        if (buy) {
            double price = currentState.getJSONObject("prices").getJSONObject("BTCUSD").getDouble("bybit") + 1;
            BybitClient.placeLimitOrder("BTCUSD", "Buy", price, quantity, -1, -1);
        } else {
            double price = currentState.getJSONObject("prices").getJSONObject("BTCUSD").getDouble("bybit") - 1;
            BybitClient.placeLimitOrder("BTCUSD", "Sell", price, quantity, -1, -1);
        }
    }
}
