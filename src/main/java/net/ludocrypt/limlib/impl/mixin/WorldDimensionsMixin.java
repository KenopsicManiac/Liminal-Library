package net.ludocrypt.limlib.impl.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Sets;

import net.ludocrypt.limlib.api.LimlibWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;

@Mixin(WorldDimensions.class)
public class WorldDimensionsMixin {

	@Shadow
	@Final
	@Mutable
	private static Set<ResourceKey<LevelStem>> BUILTIN_ORDER;

	@Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Set;size()I", shift = Shift.BEFORE, ordinal = 0))
	private static void limlib$clinit(CallbackInfo ci) {
		Set<ResourceKey<LevelStem>> dimensions = Sets.newHashSet();
		dimensions.addAll(BUILTIN_ORDER);
		LimlibWorld.LIMLIB_WORLD
			.entrySet()
			.forEach((entry) -> dimensions.add(ResourceKey.create(Registries.LEVEL_STEM, entry.getKey().location())));
		BUILTIN_ORDER = dimensions;
	}

}
