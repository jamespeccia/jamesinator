package websocket;

import logger.Logger;

import java.io.IOException;
import java.util.TimerTask;

public class HeartbeatService extends TimerTask {

    private final int rate;
    private final String heartbeat;
    private final WSClient wsc;
    private boolean isRunning;

    public HeartbeatService(WSClient wsc, String heartbeat, int rate) {
        this.wsc = wsc;
        this.heartbeat = heartbeat;
        this.rate = rate;
        this.isRunning = true;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        if (time - wsc.lastMessageTime > rate + 2000) {
            wsc.onError(wsc.session, new Throwable("Did not receive a Ping response from " + wsc.EXCHANGE));
            this.isRunning = false;
        }

        if(this.isRunning) {
            try {
                wsc.session.getBasicRemote().sendText(heartbeat);
            } catch (IOException e) {
                Logger.log(wsc.EXCHANGE + " heartbeat interrupted.", e.toString());
            }
        }

    }
}
