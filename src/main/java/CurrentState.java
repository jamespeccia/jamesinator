import org.json.JSONObject;

public class CurrentState {

    public static JSONObject initialize() {
        final JSONObject CURRENT_STATE = new JSONObject();
        CURRENT_STATE.put("prices", new JSONObject());
        CURRENT_STATE.getJSONObject("prices").put("BTCUSD", new JSONObject());
        CURRENT_STATE.getJSONObject("prices").put("ETHUSD", new JSONObject());
        CURRENT_STATE.getJSONObject("prices").put("XRPUSD", new JSONObject());
        CURRENT_STATE.put("lastUpdated", new JSONObject());
        CURRENT_STATE.getJSONObject("lastUpdated").put("BTCUSD", new JSONObject());
        CURRENT_STATE.getJSONObject("lastUpdated").put("ETHUSD", new JSONObject());
        CURRENT_STATE.getJSONObject("lastUpdated").put("XRPUSD", new JSONObject());
        return CURRENT_STATE;
    }
}
