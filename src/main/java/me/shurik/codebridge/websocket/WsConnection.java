package me.shurik.codebridge.websocket;

import com.mojang.brigadier.suggestion.Suggestion;
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
        System.out.println("[WS] [🔺] " + message);
        webSocket.send(message.toString());
    }

    public int id() {
        return id;
    }

    public void sendError(String message) {
        send(WsMessage.error(id, message));
    }

    public void sendCompletionResponse(Collection<Suggestion> suggestions) {
        send(WsMessage.completionResponse(id, suggestions));
    }

    public void sendInfoResponse(String gameVersion, String username, boolean inSingleplayer, int datapackVersion) {
        send(WsMessage.infoResponse(id, gameVersion, username, inSingleplayer, datapackVersion));
    }
}
