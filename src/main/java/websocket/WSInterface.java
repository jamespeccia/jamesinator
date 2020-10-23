package websocket;

import javax.websocket.*;
import java.io.IOException;

interface WSInterface {

    void connect();

    void send(String message) throws IOException;

    @OnOpen
    void onOpen(Session session);

    @OnClose
    void onClose(Session session, CloseReason closeReason);

    @OnMessage
    void onMessage(Session session, String msg);

    void subscribe();

}
