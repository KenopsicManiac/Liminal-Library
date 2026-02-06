package net.ludocrypt.limlib.impl.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.ludocrypt.limlib.api.LimLibRegistries;
import net.minecraft.client.DeltaTracker;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.impl.bridge.IrisBridge;
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

		LookupGrabber.snatchFromLevel(client.level, LimLibRegistries.SKYBOX).ifPresent(sky -> {
			var poseStack = new PoseStack();
			poseStack.mulPose(positionMatrix);

			sky.renderSky(((LevelRenderer) (Object) this), client, poseStack, projectionMatrix, deltaTracker.getGameTimeDeltaPartialTick(false));
		});
	}

}
