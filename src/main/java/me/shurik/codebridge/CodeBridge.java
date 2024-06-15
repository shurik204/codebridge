package me.shurik.codebridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.suggestion.Suggestion;
import me.shurik.codebridge.data.SuggestionTypeAdapter;
import me.shurik.codebridge.websocket.CodeBridgeWsServer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CodeBridge {
    public static final String MOD_ID = "codebridge";
    public static final String MOD_NAME = "CodeBridge";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Logger WS_LOGGER = LoggerFactory.getLogger(MOD_NAME + "-WS");
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static CodeBridgeWsServer wsServer;
    public static boolean wsClientConnected = false;
    public static final Gson GSON;

    public static void init() {
        wsServer = new CodeBridgeWsServer(59039);
        wsServer.start();
    }

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Suggestion.class, new SuggestionTypeAdapter());
        GSON = gsonBuilder.create();
    }
}