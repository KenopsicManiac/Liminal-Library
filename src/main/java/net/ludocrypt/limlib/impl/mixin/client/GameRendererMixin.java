package net.ludocrypt.limlib.impl.mixin.client;

import java.util.function.Function;

import net.ludocrypt.limlib.api.LimLibRegistries;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.impl.shader.PostProcesser;
import net.ludocrypt.limlib.impl.shader.PostProcesserManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Final
	Minecraft minecraft;

	@Unique
	private final Function<ResourceLocation, PostProcesser> memoizedShaders = Util.memoize(PostProcesserManager.INSTANCE::find);

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V", shift = Shift.AFTER))
	private void limlib$render(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
		var level = minecraft.level;
		var registry = level.registryAccess();
		var location = level.dimension().location();

		LookupGrabber.snatchFromLevel(minecraft.level, LimLibRegistries.POST_EFFECT)
			.filter(PostEffect::shouldRender).ifPresent(postEffect -> {
				postEffect.beforeRender();
				memoizedShaders.apply(postEffect.getShaderLocation()).render(deltaTracker.getGameTimeDeltaPartialTick(false));
			});
	}

}
