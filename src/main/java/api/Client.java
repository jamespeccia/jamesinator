package api;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public abstract class Client {
    public static JSONObject placeLimitOrder(
            String orderSymbol,
            String orderSide,
            double orderPrice,
            int orderQuantity,
            double takeProfit,
            double stopLoss) {
        return null;
    }

    public static JSONObject placeMarketOrder(
            String orderSymbol, String orderSide, int orderQuantity, double takeProfit, double stopLoss) {
        return null;
    }

    public static JSONObject modifyActiveOrder(
            String orderSymbol, String orderID, double newPrice, int newQuantity) {
        return null;
    }

    public static JSONObject getOrderStatus(String orderSymbol, String orderID) {
        return null;
    }

    public static String sign(String parameters, String SECRET)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(parameters.getBytes()));
    }
}
