package api;

import logger.Logger;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class BybitClient extends Client {

    private static final String API_KEY = "cXUibe7cBKWRORHxDA";
    private static final String SECRET = "FL8o4DDVN4uDMAZWaFYG73wHm8amf0zPHVI0";
    private static final String BASE_ENDPOINT = "api.bybit.com";
    private static final HttpClient httpClient = HttpClients.createMinimal();

    public static JSONObject placeLimitOrder(String orderSymbol, String orderSide, double orderPrice, int orderQuantity, double takeProfit, double stopLoss) {
        return placeActiveOrder(orderSymbol, orderSide, "Limit", orderPrice, orderQuantity, takeProfit, stopLoss);
    }

    public static JSONObject placeMarketOrder(String orderSymbol, String orderSide, int orderQuantity, double takeProfit, double stopLoss) {
        return placeActiveOrder(orderSymbol, orderSide, "Market", -1, orderQuantity, takeProfit, stopLoss);
    }

    private static JSONObject placeActiveOrder(String orderSymbol, String orderSide, String orderType, double orderPrice, int orderQuantity, double takeProfit, double stopLoss) {

        String query = "api_key=" + API_KEY +
                "&order_type=" + orderType +
                "&price=" + orderPrice +
                "&qty=" + orderQuantity +
                "&side=" + orderSide +
                "&symbol=" + orderSymbol +
                "&time_in_force=FillOrKill" +
                "&timestamp=" + System.currentTimeMillis();

        String signature = null;
        try {
            signature = sign(query);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Logger.log("An error was thrown while signing a Bybit API request. Order was never placed. (" + e.toString() + ")");
            return null;
        }

        String URL = "https://" + BASE_ENDPOINT + "/v2/private/order/create?" + query + "&sign=" + signature;

        HttpPost httpPost = new HttpPost(URL); // 2ms

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost); // 440ms
        } catch (IOException e) {
            Logger.log("An error was thrown while attempting to place an order on Bybit. Unknown if order was placed. (" + e.toString() + ")");
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

    public static JSONObject modifyActiveOrder(String orderSymbol, String orderID, double newPrice, int newQuantity) {

        try {
            List<NameValuePair> payload = new ArrayList<>();

            payload.add(new BasicNameValuePair("api_key", API_KEY));
            payload.add(new BasicNameValuePair("order_id", orderID));

            if (newPrice != -1)
                payload.add(new BasicNameValuePair("p_r_price", String.valueOf(newPrice)));

            if (newQuantity != -1)
                payload.add(new BasicNameValuePair("p_r_qty", String.valueOf(newQuantity)));

            payload.add(new BasicNameValuePair("symbol", orderSymbol));
            payload.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));

            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/open-api/order/replace")
                    .setParameters(payload)
                    .build();

            payload.add(new BasicNameValuePair("sign", sign(uri.getRawQuery())));

            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/open-api/order/replace")
                    .setParameters(payload)
                    .build();

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getOrderStatus(String orderSymbol, String orderID) {

        try {
            List<NameValuePair> payload = new ArrayList<>();

            payload.add(new BasicNameValuePair("api_key", API_KEY));
            payload.add(new BasicNameValuePair("order_id", orderID));
            payload.add(new BasicNameValuePair("symbol", orderSymbol));
            payload.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));

            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/v2/private/order")
                    .setParameters(payload)
                    .build();

            payload.add(new BasicNameValuePair("sign", sign(uri.getRawQuery())));

            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_ENDPOINT)
                    .setPath("/v2/private/order")
                    .setParameters(payload)
                    .build();

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void ping() {
        long start = System.currentTimeMillis();
        try {
            InetAddress address = InetAddress.getByName("web.mit.edu");
            System.out.println("Name: " + address.getHostName());
            System.out.println("Addr: " + address.getHostAddress());
            System.out.println("Reach: " + address.isReachable(3000));
        }
        catch (UnknownHostException e) {
            System.err.println("Unable to lookup web.mit.edu");
        }
        catch (IOException e) {
            System.err.println("Unable to reach web.mit.edu");
        }
        long end = System.currentTimeMillis();

        System.out.println(end-start);
    }

    private static String sign(String parameters) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(parameters.getBytes()));
    }
}

