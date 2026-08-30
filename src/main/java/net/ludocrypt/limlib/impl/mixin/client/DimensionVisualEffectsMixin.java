package net.ludocrypt.limlib.impl.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.level.dimension.DimensionType;

@Mixin(DimensionSpecialEffects.class)
public class DimensionVisualEffectsMixin {

	@Inject(method = "forType", at = @At("HEAD"), cancellable = true)
	private static void limlib$byDimensionType(DimensionType dimensionType,
			CallbackInfoReturnable<DimensionSpecialEffects> ci) {
		LookupGrabber.snatch(DimensionEffects.MIXIN_WORLD_LOOKUP.get(), dimensionType.effectsLocation())
			.map(DimensionEffects::getDimensionEffects)
			.ifPresent(ci::setReturnValue);
	}

}
