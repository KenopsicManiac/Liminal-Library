package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Optional;
import java.util.function.Function;

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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Unique
	private final Function<ResourceLocation, PostProcesser> memoizedShaders = Util
		.memoize(id -> PostProcesserManager.INSTANCE.find(id));

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V", shift = Shift.AFTER))
	private void limlib$render(float tickDelta, long nanoTime, boolean renderLevel, CallbackInfo info) {
		Optional<PostEffect> optionalPostEffect = LookupGrabber
			.snatch(minecraft.level.registryAccess().lookup(PostEffect.POST_EFFECT_KEY).get(),
				ResourceKey.create(PostEffect.POST_EFFECT_KEY, minecraft.level.dimension().location()));

		if (optionalPostEffect.isPresent()) {
			PostEffect postEffect = optionalPostEffect.get();

			if (postEffect.shouldRender()) {
				postEffect.beforeRender();
				memoizedShaders.apply(postEffect.getShaderLocation()).render(tickDelta);
			}

		}

	}

}
