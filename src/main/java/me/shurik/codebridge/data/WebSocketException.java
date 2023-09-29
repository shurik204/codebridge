package me.shurik.codebridge.data;

import org.jetbrains.annotations.Nullable;

public class WebSocketException extends RuntimeException {
    public final int id;
    @Nullable
    public final Exception ex;
    public WebSocketException(int id, String message) {
        this(id, message, null);
    }
    public WebSocketException(int id, Exception ex) {
        this(id, ex.getMessage(), ex);
    }
    public WebSocketException(int id, String message, @Nullable Exception ex) {
        super(message);
        this.id = id;
        this.ex = ex;
    }
}