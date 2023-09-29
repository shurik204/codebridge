package me.shurik.codebridge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.slf4j.Logger;
import org.java_websocket.drafts.Draft_6455;
import me.shurik.codebridge.data.WebSocketException;

/**
 * Hack to have better stack traces
 * for custom exception thrown in websocket
 */
@Mixin(value = Draft_6455.class, remap = false)
public class Draft_6455Mixin {
    @Redirect(method = "logRuntimeException", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V"))
    void logRuntimeException(Logger instance, String string, Throwable exc) {
        if (exc instanceof WebSocketException wsE && wsE.ex != null) {
            instance.error(string, wsE.ex);
        } else {
            instance.error(string, exc);
        }
    }
}