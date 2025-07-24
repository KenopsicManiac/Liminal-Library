package net.ludocrypt.limlib.impl.mixin;

import net.minecraft.data.worldgen.BootstrapContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.LimlibWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;

@Mixin(DimensionTypes.class)
public class BuiltinDimensionTypesMixin {

	@Inject(method = "bootstrap", at = @At("RETURN"))
	private static void limlib$initAndGetDefault(BootstrapContext<DimensionType> bootstrapContext, CallbackInfo ci) {
		LimlibWorld.LIMLIB_WORLD
			.entrySet()
			.forEach((entry) -> bootstrapContext
				.register(ResourceKey.create(Registries.DIMENSION_TYPE, entry.getKey().location()),
					entry.getValue().getDimensionTypeSupplier().get()));
	}

}
