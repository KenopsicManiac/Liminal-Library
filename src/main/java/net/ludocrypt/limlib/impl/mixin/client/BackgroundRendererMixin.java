package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Optional;
import java.util.function.Function;

import net.ludocrypt.limlib.api.LimLibRegistries;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
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

		return LookupGrabber.snatchFromLevel(Minecraft.getInstance().level, LimLibRegistries.DIMENSION_EFFECTS)
			.map(DimensionEffects::skyShading)
			.orElse(in);
	}

}
