package api;

import java.io.IOException;
import java.net.URI;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import logger.Logger;

public class PhemexClient extends Client {

//    public static final String API_KEY = "bdd1a81e-cedb-417c-8232-9fe242aeef1b";
//    public static final String SECRET =
//            "Z7AZhsVsZw-zbTDTO2OU5MeqhtVY8nPdwt7eqGTCHAFiZjY0OWVhOC0wZDI3LTQwMjMtYmU3Ny0wZDUyYTIwZTNkMDI";
    public static final String API_KEY = "d1bc3f1a-99ee-4905-8ba0-0f1b806f1233";
    public static final String SECRET =
            "V2pyDj7AZGOjIOwwUVLI3tNKt7zwlCe2Nh7JpSTCuMJiOTFhM2JmZC1iMTFjLTQ5YWItOTIyOC1kMGE2NmM5MjY4MmQ";
    public static final String BASE_ENDPOINT = "testnet-api.phemex.com";
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

        String body =
                "{\"clOrdID\":\""
                        + API_KEY
                        + "\","
                        + "\"ordType\":\""
                        + orderType
                        + "\","
                        + "\"orderQty\":"
                        + orderQuantity
                        + ","
                        + "\"price\":"
                        + orderPrice
                        + ","
                        + "\"side\":\""
                        + orderSide
                        + "\","
                        + "\"symbol\":\""
                        + orderSymbol
                        + "\","
                        + "\"timeInForce\":\"FillOrKill\"}";

        String URL = "https://" + BASE_ENDPOINT + "/orders";

        String expiry = String.valueOf(System.currentTimeMillis() * 1000);

        String signature = null;
        try {
            signature = sign("/orders" + expiry + body, SECRET);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Logger.log(
                    "An error was thrown while signing a Phemex API request. Order was never placed. ("
                            + e.toString()
                            + ")");
            return null;
        }

        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("content-type", "application/json");
        httpPost.setHeader("x-phemex-access-token", API_KEY);
        httpPost.setHeader("x-phemex-request-expiry", expiry);
        httpPost.setHeader("x-phemex-request-signature", signature);
        httpPost.setEntity(new StringEntity(body, "UTF-8"));

        HttpClient httpClient = HttpClients.createMinimal();

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            Logger.log(
                    "An error was thrown while attempting to place an order on Phemex. Unknown if order was placed. ("
                            + e.toString()
                            + ")");
            return null;
        }

        String responseString = null;
        try {
            responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (IOException e) {
            Logger.log(
                    "An error was thrown while attempting to parse a Phemex response. ("
                            + e.toString()
                            + ")");
            return null;
        }

        return new JSONObject(responseString);
    }

//    public static JSONObject modifyActiveOrder(
//            String orderSymbol, String orderID, double newPrice, int newQuantity) {
//
//        try {
//            List<NameValuePair> payload = new ArrayList<>();
//
//            payload.add(new BasicNameValuePair("api_key", API_KEY));
//            payload.add(new BasicNameValuePair("order_id", orderID));
//
//            if (newPrice != -1)
//                payload.add(new BasicNameValuePair("p_r_price", String.valueOf(newPrice)));
//
//            if (newQuantity != -1)
//                payload.add(new BasicNameValuePair("p_r_qty", String.valueOf(newQuantity)));
//
//            payload.add(new BasicNameValuePair("symbol", orderSymbol));
//            payload.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
//
//            URI uri =
//                    new URIBuilder()
//                            .setScheme("https")
//                            .setHost(BASE_ENDPOINT)
//                            .setPath("/open-api/order/replace")
//                            .setParameters(payload)
//                            .build();
//
//            payload.add(new BasicNameValuePair("sign", sign(uri.getRawQuery())));
//
//            uri =
//                    new URIBuilder()
//                            .setScheme("https")
//                            .setHost(BASE_ENDPOINT)
//                            .setPath("/open-api/order/replace")
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

}
