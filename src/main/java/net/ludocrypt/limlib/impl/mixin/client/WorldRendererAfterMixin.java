package net.ludocrypt.limlib.impl.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import java.util.function.Consumer;

import net.ludocrypt.limlib.api.LimLibRegistries;
import net.minecraft.client.DeltaTracker;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.impl.bridge.IrisBridge;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceKey;

@Mixin(value = LevelRenderer.class, priority = 1050)
public abstract class WorldRendererAfterMixin {
	@Inject(method = "renderLevel", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
	private void limlib$render$clear(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {

		if (IrisBridge.IRIS_LOADED) {

			if (IrisBridge.areShadersInUse()) {
				Minecraft client = Minecraft.getInstance();

				LookupGrabber.snatchFromLevel(client.level, LimLibRegistries.SKYBOX).ifPresent(sky -> {
					var matrices = new PoseStack();
					matrices.mulPose(positionMatrix);

					sky.renderSky(((LevelRenderer) (Object) this), client, matrices, projectionMatrix, deltaTracker.getGameTimeDeltaPartialTick(false));
				});
			}

		}

	}

}
