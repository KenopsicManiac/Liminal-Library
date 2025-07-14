package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

@Mixin(value = LevelRenderer.class, priority = 1050)
public abstract class WorldRendererAfterMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "renderLevel", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
	private void limlib$render$clear(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
									 Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f positionMatrix,
									 CallbackInfo ci) {

		if (IrisBridge.IRIS_LOADED) {

			if (IrisBridge.areShadersInUse()) {
				Optional<Skybox> sky = LookupGrabber
					.snatch(minecraft.level.registryAccess().lookup(Skybox.SKYBOX_KEY).get(),
						ResourceKey.create(Skybox.SKYBOX_KEY, minecraft.level.dimension().location()));

				sky.ifPresent(skybox -> skybox.renderSky(((LevelRenderer) (Object) this), minecraft, matrices, positionMatrix, tickDelta));

			}

		}

	}

}
