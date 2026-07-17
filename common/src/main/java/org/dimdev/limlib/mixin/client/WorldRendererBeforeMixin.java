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

@Mixin(value = LevelRenderer.class, priority = 950)
public abstract class WorldRendererBeforeMixin {

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V", ordinal = 0, shift = At.Shift.AFTER, remap = false))
	private void limlib$render$clear(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {

		if (IrisBridge.IRIS_LOADED) {

			if (IrisBridge.areShadersInUse()) {
				return;
			}

		}

		Minecraft client = Minecraft.getInstance();

		LookupGrabber.snatchFromLevel(client.level, LimLibRegistryKeys.SKYBOX).ifPresent(sky -> {
			var renderer = EffectRenderers.get(sky);

			if(renderer != null) {
				var poseStack = new PoseStack();
				poseStack.mulPose(positionMatrix);


				renderer.renderSky(sky, ((LevelRenderer) (Object) this), client, poseStack, projectionMatrix, deltaTracker.getGameTimeDeltaPartialTick(false));
			}
		});
	}

}
