package me.shurik.codebridge.websocket;

import com.google.gson.*;
import me.shurik.codebridge.CodeBridge;
import me.shurik.codebridge.Utils;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;

import java.util.Collection;

public record WsMessage(int id, WsMessageType type, JsonObject data) {
    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        object.add("id", new JsonPrimitive(id));
        object.add("type", new JsonPrimitive(type.toString()));
        object.add("data", data);
        return object.toString();
    }

    // Static constructors
    public static WsMessage parse(String string) {
        JsonObject json = Utils.parseStrict(string);
        WsMessageType event = Enum.valueOf(WsMessageType.class, json.get("type").getAsString());
        return new WsMessage(json.get("id").getAsInt(), event, json.get("data").getAsJsonObject());
    }

    public static WsMessage infoResponse(int id, String gameVersion, String username, boolean inSingleplayer, int datapackVersion) {
        JsonObject data = new JsonObject();
        data.add("game_version", new JsonPrimitive(SharedConstants.getGameVersion().getName()));
        data.add("player_name", new JsonPrimitive(CodeBridge.client.getGameProfile().getName()));
        data.add("is_singleplayer", new JsonPrimitive(CodeBridge.client.isInSingleplayer()));
        data.add("datapack_version", new JsonPrimitive(SharedConstants.getGameVersion().getResourceVersion(ResourceType.SERVER_DATA)));
        return new WsMessage(id, WsMessageType.INFO_RESPONSE, data);
    }

    public static WsMessage completionResponse(int id, Collection<String> suggestionsList) {
        JsonObject data = new JsonObject();
        data.add("suggestions", new Gson().toJsonTree(suggestionsList));
        return new WsMessage(id, WsMessageType.COMPLETION_RESPONSE, data);
    }

    public static WsMessage error(int id, String message) {
        JsonObject data = new JsonObject();
        data.add("error_message", new JsonPrimitive(message));
        return new WsMessage(id, WsMessageType.ERROR, data);
    }
}