package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Optional;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceKey;

@Mixin(FogRenderer.class)
public abstract class BackgroundRendererMixin {


	@ModifyVariable(method = "setupColor", at = @At(value = "STORE", ordinal = 3), ordinal = 2)
	private static float limlib$modifySkyColor(float in) {
		Minecraft client = Minecraft.getInstance();

		Optional<DimensionEffects> dimensionEffects = LookupGrabber
			.snatch(client.level.registryAccess().lookup(DimensionEffects.DIMENSION_EFFECTS_KEY).get(),
				ResourceKey.create(DimensionEffects.DIMENSION_EFFECTS_KEY, client.level.dimension().location()));

		if (dimensionEffects.isPresent()) {
			return dimensionEffects.get().getSkyShading();
		}

		return in;
	}

}
