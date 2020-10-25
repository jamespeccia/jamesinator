import org.json.JSONObject;

public class State extends JSONObject {

    public State() {
        super();
        this.put("status", "ok");
        this.put("prices", new JSONObject());
        this.getJSONObject("prices").put("BTCUSD", new JSONObject());
        this.getJSONObject("prices").put("ETHUSD", new JSONObject());
        this.getJSONObject("prices").put("XRPUSD", new JSONObject());
        this.put("last_updated", new JSONObject());
        this.getJSONObject("last_updated").put("BTCUSD", new JSONObject());
        this.getJSONObject("last_updated").put("ETHUSD", new JSONObject());
        this.getJSONObject("last_updated").put("XRPUSD", new JSONObject());
    }


    public void setPrice(String symbol, String exchange, double price) {
        this.getJSONObject("prices").getJSONObject(symbol).put(exchange, price);
    }

    public double getPrice(String symbol, String exchange) {
        return this.getJSONObject("prices").getJSONObject(symbol).getDouble(exchange);
    }

    public void lastUpdatedNow(String symbol, String exchange, double price) {
        this.getJSONObject("last_updated").getJSONObject(symbol).put(exchange, System.currentTimeMillis());
    }

    public long getLastUpdateTime(String symbol, String exchange, double price) {
        return this.getJSONObject("last_updated").getJSONObject(symbol).getLong(exchange);
    }
}
