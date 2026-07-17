package org.dimdev.limlib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;

import org.dimdev.limlib.api.LimLibRegistryKeys;
import net.minecraft.client.DeltaTracker;
import org.dimdev.limlib.api.client.effect.EffectRenderers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.dimdev.limlib.api.effects.LookupGrabber;
import org.dimdev.limlib.impl.bridge.IrisBridge;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;

@Mixin(value = LevelRenderer.class, priority = 1050)
public abstract class WorldRendererAfterMixin {
	@Inject(method = "renderLevel", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
	private void limlib$render$clear(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {

		if (IrisBridge.IRIS_LOADED) {

			if (IrisBridge.areShadersInUse()) {
				Minecraft client = Minecraft.getInstance();

				LookupGrabber.snatchFromLevel(client.level, LimLibRegistryKeys.SKYBOX).ifPresent(sky -> {
					var renderer = EffectRenderers.get(sky);

					if(renderer != null) {
						var matrices = new PoseStack();
						matrices.mulPose(positionMatrix);

						renderer.renderSky(sky, ((LevelRenderer) (Object) this), client, matrices, projectionMatrix, deltaTracker.getGameTimeDeltaPartialTick(false));
					}
				});
			}

		}

	}

}
