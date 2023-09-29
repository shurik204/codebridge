package me.shurik.codebridge.websocket;

import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;

import java.util.Collection;

public class WsConnection {
    private final WebSocket webSocket;
    private final int id;
    public WsConnection(WebSocket webSocket, int id) {
        this.webSocket = webSocket;
        this.id = id;
    }

    public void send(WsMessage message) {
        //                             🔺
        System.out.println("[WS] [\uD83D\uDD3A] " + message);
        webSocket.send(message.toString());
    }

    public int id() {
        return id;
    }

    public void sendError(String message) {
        send(WsMessage.error(id, message));
    }

    public void sendCompletionResponse(Collection<String> suggestions) {
        send(WsMessage.completionResponse(id, suggestions));
    }

    public void sendInfoResponse(String gameVersion, String username, boolean inSingleplayer, int datapackVersion) {
        send(WsMessage.infoResponse(id, gameVersion, username, inSingleplayer, datapackVersion));
    }
}
