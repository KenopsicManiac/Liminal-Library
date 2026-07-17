package org.dimdev.limlib.mixin.client;

import java.util.function.Function;

import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.api.client.effect.EffectRenderers;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.dimdev.limlib.api.effects.LookupGrabber;
import org.dimdev.limlib.post.PostEffect;
import org.dimdev.limlib.impl.shader.PostProcesser;
import org.dimdev.limlib.impl.shader.PostProcesserManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Unique
	private final Function<ResourceLocation, PostProcesser> memoizedShaders = Util.memoize(PostProcesserManager.INSTANCE::find);

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V", shift = Shift.AFTER))
	private void limlib$render(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
		LookupGrabber.snatchFromLevel(minecraft.level, LimLibRegistryKeys.POST_EFFECT)
			.filter(PostEffect::shouldRender).ifPresent(postEffect -> {
				var renderer = EffectRenderers.get(postEffect);

				if(renderer != null) {
					renderer.beforeRender(postEffect);
					memoizedShaders.apply(postEffect.getShaderLocation()).render(deltaTracker.getGameTimeDeltaPartialTick(false));
				}
			});
	}

}
