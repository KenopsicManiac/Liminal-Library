package net.ludocrypt.limlib.impl.debug;

import com.mojang.serialization.Lifecycle;
import net.ludocrypt.limlib.api.LimlibRegistrar;
import net.ludocrypt.limlib.api.LimlibRegistryHooks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Map;

public class DebugWorld implements LimlibRegistrar {

	public static final ResourceKey<WorldPreset> DEBUG_KEY = ResourceKey
		.create(Registries.WORLD_PRESET, new ResourceLocation("limlib", "debug_nbt"));

	@Override
	public void registerHooks() {
		LimlibRegistryHooks
			.hook(Registries.WORLD_PRESET, (infoLookup, registryKey, registry) -> registry
				.register(DEBUG_KEY, new WorldPreset(Map
					.of(LevelStem.OVERWORLD,
						new LevelStem(
							infoLookup
								.lookup(Registries.DIMENSION_TYPE)
								.get()
								.getter()
								.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
							new DebugNbtChunkGenerator(
								infoLookup.lookup(Registries.BIOME).get().getter().getOrThrow(net.minecraft.world.level.biome.Biomes.THE_VOID))))),
					Lifecycle.stable()));
	}

}
