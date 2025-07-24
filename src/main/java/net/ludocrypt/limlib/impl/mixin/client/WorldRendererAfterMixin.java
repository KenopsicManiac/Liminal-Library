package net.ludocrypt.limlib.impl.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;

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
	private void limlib$render$clear(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
			Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f positionMatrix,
			CallbackInfo ci) {

		if (IrisBridge.IRIS_LOADED) {

			if (IrisBridge.areShadersInUse()) {
				Minecraft client = Minecraft.getInstance();

				Optional<Skybox> sky = LookupGrabber
					.snatch(client.level.registryAccess().lookup(Skybox.SKYBOX_KEY).get(),
						ResourceKey.create(Skybox.SKYBOX_KEY, client.level.dimension().location()));

				if (sky.isPresent()) {
					sky.get().renderSky(((LevelRenderer) (Object) this), client, matrices, positionMatrix, tickDelta);
				}

			}

		}

	}

}
