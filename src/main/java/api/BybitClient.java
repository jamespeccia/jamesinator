package api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import logger.Logger;

public class BybitClient extends Client {

    public static final String API_KEY = "TQISHzK6jUBYZIcKyO";
    public static final String SECRET = "n6DTM4WXNGDWLYn6gLVkCR3Wzn9NIg1pEFN6";
    public static final String BASE_ENDPOINT = "api-testnet.bybit.com";
    private static final HttpClient httpClient = HttpClients.createMinimal();

    public static JSONObject placeLimitOrder(
            String orderSymbol,
            String orderSide,
            double orderPrice,
            int orderQuantity,
            double takeProfit,
            double stopLoss) {
        return placeActiveOrder(
                orderSymbol, orderSide, "Limit", orderPrice, orderQuantity, takeProfit, stopLoss);
    }

    public static JSONObject placeMarketOrder(
            String orderSymbol, String orderSide, int orderQuantity, double takeProfit, double stopLoss) {
        return placeActiveOrder(
                orderSymbol, orderSide, "Market", -1, orderQuantity, takeProfit, stopLoss);
    }

    private static JSONObject placeActiveOrder(
            String orderSymbol,
            String orderSide,
            String orderType,
            double orderPrice,
            int orderQuantity,
            double takeProfit,
            double stopLoss) {

        String query =
                "api_key="
                        + API_KEY
                        + "&order_type="
                        + orderType
                        + "&price="
                        + orderPrice
                        + "&qty="
                        + orderQuantity
                        + "&side="
                        + orderSide
                        + "&symbol="
                        + orderSymbol
                        + "&time_in_force=FillOrKill"
                        + "&timestamp="
                        + System.currentTimeMillis();

        String signature = null;
        try {
            signature = sign(query, SECRET);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Logger.log(
                    "An error was thrown while signing a Bybit API request. Order was never placed. ("
                            + e.toString()
                            + ")");
            return null;
        }

        String URL = "https://" + BASE_ENDPOINT + "/v2/private/order/create?" + query + "&sign=" + signature;

        HttpPost httpPost = new HttpPost(URL); // 2ms

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost); // 440ms
        } catch (IOException e) {
            Logger.log(
                    "An error was thrown while attempting to place an order on Bybit. Unknown if order was placed. ("
                            + e.toString()
                            + ")");
            return null;
        }

        String responseString = null;
        try {
            responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (IOException e) {
            Logger.log("An error was thrown while attempting to parse a Bybit response. (" + e.toString() + ")");
            return null;
        }

        return new JSONObject(responseString);
    }


//    public static JSONObject getOrderStatus(String orderSymbol, String orderID) {
//
//        try {
//            List<NameValuePair> payload = new ArrayList<>();
//
//            payload.add(new BasicNameValuePair("api_key", API_KEY));
//            payload.add(new BasicNameValuePair("order_id", orderID));
//            payload.add(new BasicNameValuePair("symbol", orderSymbol));
//            payload.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
//
//            URI uri =
//                    new URIBuilder()
//                            .setScheme("https")
//                            .setHost(BASE_ENDPOINT)
//                            .setPath("/v2/private/order")
//                            .setParameters(payload)
//                            .build();
//
//            payload.add(new BasicNameValuePair("sign", sign(uri.getRawQuery())));
//
//            uri =
//                    new URIBuilder()
//                            .setScheme("https")
//                            .setHost(BASE_ENDPOINT)
//                            .setPath("/v2/private/order")
//                            .setParameters(payload)
//                            .build();
//
//            return null;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//



}
