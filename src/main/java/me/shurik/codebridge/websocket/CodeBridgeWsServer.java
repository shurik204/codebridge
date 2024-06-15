package me.shurik.codebridge.websocket;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestions;
import me.shurik.codebridge.CodeBridge;
import net.minecraft.SharedConstants;
import net.minecraft.command.CommandSource;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class CodeBridgeWsServer extends EventBasedWsServer {
    public CodeBridgeWsServer(int port) {
        super(port);

        addMessageHandler(WsMessageType.INFO_REQUEST.toString(), this::onInfoRequest);
        addMessageHandler(WsMessageType.COMPLETION_REQUEST.toString(), this::onCompletionRequest);
    }

    private void onInfoRequest(WsConnection connection, JsonObject data) {
        connection.sendInfoResponse(
                SharedConstants.getGameVersion().getName(),
                CodeBridge.client.getGameProfile().getName(),
                CodeBridge.client.isInSingleplayer(),
                SharedConstants.getGameVersion().getResourceVersion(ResourceType.SERVER_DATA)
        );
    }

    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestions;
    private void onCompletionRequest(WsConnection connection, JsonObject data) {
        String command = data.get("command").getAsString();
        int cursor = data.get("cursor").getAsInt();

        CommandDispatcher<CommandSource> commandDispatcher = CodeBridge.client.player.networkHandler.getCommandDispatcher();
        ParseResults<CommandSource> parse = commandDispatcher.parse(command, CodeBridge.client.player.networkHandler.getCommandSource());

        this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(parse, command.length());
        this.pendingSuggestions.thenRun(() -> {
            if (this.pendingSuggestions.isDone()) {
                this.sendCommandSuggestions(connection);
            }
        });
    }

    private void sendCommandSuggestions(WsConnection connection) {
        if (this.pendingSuggestions == null) {
            connection.sendCompletionResponse(Collections.emptyList());
            return;
        }

        connection.sendCompletionResponse(this.pendingSuggestions.join().getList());
    }
}