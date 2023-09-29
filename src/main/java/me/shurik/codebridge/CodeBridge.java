package me.shurik.codebridge;

import me.shurik.codebridge.websocket.CodeBridgeWsServer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CodeBridge {
    public static final String MOD_ID = "codebridge";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static CodeBridgeWsServer wsServer;
    public static boolean wsClientConnected = false;

    public static void init() {
        wsServer = new CodeBridgeWsServer(51039);
        wsServer.start();
    }
}