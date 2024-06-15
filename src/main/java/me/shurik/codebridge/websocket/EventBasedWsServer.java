package me.shurik.codebridge.websocket;

import com.google.gson.JsonObject;
import me.shurik.codebridge.CodeBridge;
import me.shurik.codebridge.exception.WebSocketException;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static me.shurik.codebridge.CodeBridge.LOGGER;
import static me.shurik.codebridge.CodeBridge.WS_LOGGER;

public class EventBasedWsServer extends WebSocketServer {
    private final Map<String, Event<MessageHandler>> messageSubscriptions = new HashMap<>();
    public EventBasedWsServer(int port) { super(new InetSocketAddress(Inet4Address.getLoopbackAddress(), port)); }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        if (CodeBridge.wsClientConnected) {
            throw new InvalidDataException(CloseFrame.REFUSE, "Client already connected!");
        }
        if ((request.hasFieldValue("Origin") || !request.getResourceDescriptor().equals("/")) && !FabricLoader.getInstance().isDevelopmentEnvironment()) {
            throw new InvalidDataException(CloseFrame.REFUSE, "Connection refused");
        }

//        LOGGER.info("[WS] Client connected!");
        LOGGER.info("[WS] Client headers:");
        for (Iterator<String> it = request.iterateHttpFields(); it.hasNext(); ) {
            String str = it.next();
            LOGGER.info("[WS] - {}", str);
        }

        return super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LOGGER.info("[WS] Client connected!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        CodeBridge.wsClientConnected = false;
        WS_LOGGER.info("[WS] Client disconnected!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        WsMessage msg = WsMessage.parse(message);

        if (FabricLoader.getInstance().isDevelopmentEnvironment())
            WS_LOGGER.info("[🔻] {}", msg);

        // Client is not connected to a server
        if (CodeBridge.client.player == null) {
            conn.send(WsMessage.error(msg.id(), "Not connected to a server!").toString());
            return;
        }

        Event<MessageHandler> event = messageSubscriptions.get(msg.type().toString());
        if (event == null) {
            conn.send(WsMessage.error(msg.id(), "No such message type: " + msg.type()).toString());
            return;
        }
        event.invoker().onMessage(new WsConnection(conn, msg.id()), msg.data());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (ex instanceof BindException) {
            WS_LOGGER.info("Unable to start WebSocket on port {}!", this.getPort());
            return;
        }

        if (ex instanceof WebSocketException wsException) {
            WS_LOGGER.info("WebSocketException: {} ({})", wsException.getMessage(), wsException.id);
            conn.send(WsMessage.error(wsException.id, wsException.getMessage()).toString());
            return;
        }

        conn.send(WsMessage.error(-1, ex.getMessage()).toString());
    }

    @Override
    public void onStart() {
        WS_LOGGER.info("Started on port {}!", this.getPort());
    }

    public void addMessageHandler(String key, MessageHandler handler) {
        messageSubscriptions.computeIfAbsent(key, k -> EventFactory.createArrayBacked(MessageHandler.class, callbacks -> (connection, data) -> {
            for (MessageHandler callback : callbacks) {
                callback.onMessage(connection, data);
            }
        })).register(handler);
    }

    @FunctionalInterface
    public interface MessageHandler {
        void onMessage(WsConnection connection, JsonObject data);
    }
}
