package org.dimdev.limlib.mixin.client;

import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import org.dimdev.limlib.api.effects.LookupGrabber;
import org.dimdev.limlib.api.effects.sky.DimensionEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;

@Mixin(FogRenderer.class)
public abstract class BackgroundRendererMixin {
	@ModifyVariable(method = "setupColor", at = @At(value = "STORE", ordinal = 3), ordinal = 2)
	private static float limlib$modifySkyColor(float in) {

		return LookupGrabber.snatchFromLevel(Minecraft.getInstance().level, LimLibRegistryKeys.DIMENSION_EFFECTS)
			.map(DimensionEffects::skyShading)
			.orElse(in);
	}

}
