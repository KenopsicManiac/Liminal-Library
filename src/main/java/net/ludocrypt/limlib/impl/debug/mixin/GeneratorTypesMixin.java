package net.ludocrypt.limlib.impl.debug.mixin;

import java.util.Optional;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.WorldStem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.ludocrypt.limlib.impl.debug.DebugWorld;

@Mixin(WorldPresets.class)
public class GeneratorTypesMixin {

	@Inject(method = "fromSettings", at = @At("HEAD"), cancellable = true)
	private static void limlib$getKey(Registry<LevelStem> registry, CallbackInfoReturnable<Optional<ResourceKey<WorldPreset>>> ci) {

		Optional<ResourceKey<WorldPreset>> optional = registry
			.getOptional(LevelStem.OVERWORLD)
			.flatMap(dimensionOptions -> {
				ChunkGenerator chunkGenerator = dimensionOptions.generator();

				if (chunkGenerator instanceof DebugNbtChunkGenerator) {
					return Optional.of(DebugWorld.DEBUG_KEY);
				}

				return Optional.empty();
			});

		if (optional.isPresent()) {
			ci.setReturnValue(optional);
		}

	}

}
