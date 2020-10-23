import api.PhemexClient;
import logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import threads.BybitOrderThread;
import threads.PhemexOrderThread;
import webserver.WebServer;
import websocket.WSMain;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        final JSONObject CURRENT_STATE = CurrentState.initialize();
        new WSMain(CURRENT_STATE);
        try {
            new WebServer(5000, CURRENT_STATE).start();
        } catch (IOException e) {
            Logger.log("Unable to start webserver.", e.toString());
        }
        //run(currentState);

    }

    public static void run(JSONObject currentState) {
        final double AVERAGE = -2.5;
        final double RADIUS = 10;

        final double UPPER = AVERAGE + RADIUS;
        final double LOWER = AVERAGE - RADIUS;
        boolean inTrade = false;

        BybitOrderThread bybitOrderThread;
        PhemexOrderThread phemexOrderThread;

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // difference = bybit - phemex
            double difference = 0.0;
            try {
                difference = currentState.getJSONObject("prices").getJSONObject("BTCUSD").getDouble("bybit")
                - currentState.getJSONObject("prices").getJSONObject("BTCUSD").getDouble("phemex");
                JSONObject response = PhemexClient.placeLimitOrder("BTCUSD", "Buy", currentState.getJSONObject("prices").getJSONObject("BTCUSD").getDouble("phemex"), 1, -1, -1);
                break;
            } catch (JSONException ignored) {
            }

//            if (!inTrade && difference > UPPER) {
//                bybitOrderThread = new BybitOrderThread(currentState, false, 100);
//                phemexOrderThread = new PhemexOrderThread(currentState, true, 100);
//                bybitOrderThread.start();
//                phemexOrderThread.start();
//                inTrade = true;
//            } else if (!inTrade && difference < LOWER) {
//                bybitOrderThread = new BybitOrderThread(currentState, true, 100);
//                phemexOrderThread = new PhemexOrderThread(currentState, false, 100);
//                bybitOrderThread.start();
//                phemexOrderThread.start();
//                inTrade = true;
//            }
        }
    }
}
