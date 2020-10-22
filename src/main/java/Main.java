import api.BybitClient;
import org.json.JSONException;
import org.json.JSONObject;
import threads.BybitOrderThread;
import threads.PhemexOrderThread;
import websocket.Constants;
import websocket.WebSockets;

public class Main {
    public static void main(String[] args) {



        long start = System.currentTimeMillis();
        BybitClient.placeLimitOrder("BTCUSD", "Sell", 12000, 1, -1, -1);
        long end = System.currentTimeMillis();
        System.out.println(end-start);

//        JSONObject currentState = new JSONObject();
//        currentState.put("prices", new JSONObject());
//        WebSockets websockets = new WebSockets(currentState);
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

            System.out.println(currentState);

            // difference = bybit - phemex
            double difference = 0.0;
            try {
                difference = currentState.getJSONObject("prices").getDouble(Constants.DIFFERENCE_BTC_PRICE);
            } catch (JSONException ignored) {
            }

            if (!inTrade && difference > UPPER) {
                bybitOrderThread = new BybitOrderThread(currentState, false, 100);
                phemexOrderThread = new PhemexOrderThread(currentState, true, 100);
                bybitOrderThread.start();
                phemexOrderThread.start();
                inTrade = true;
            } else if (!inTrade && difference < LOWER) {
                bybitOrderThread = new BybitOrderThread(currentState, true, 100);
                phemexOrderThread = new PhemexOrderThread(currentState, false, 100);
                bybitOrderThread.start();
                phemexOrderThread.start();
                inTrade = true;
            }


        }


    }
}