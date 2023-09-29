package me.shurik.codebridge.mixin;

import me.shurik.codebridge.CodeBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Loader-independent way to start ws server
 */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(RunArgs args, CallbackInfo ci) {
		CodeBridge.init();
	}
}