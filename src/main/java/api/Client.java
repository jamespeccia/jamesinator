package api;


import org.json.JSONObject;

public abstract class Client {
    public static JSONObject placeLimitOrder(String orderSymbol, String orderSide, double orderPrice, int orderQuantity, double takeProfit, double stopLoss) {
        return null;
    }

    public static JSONObject placeMarketOrder(String orderSymbol, String orderSide, int orderQuantity, double takeProfit, double stopLoss) {
        return null;
    }

    public static JSONObject modifyActiveOrder(String orderSymbol, String orderID, double newPrice, int newQuantity) {
        return null;
    }

    public static JSONObject getOrderStatus(String orderSymbol, String orderID) {
        return null;
    }
}
