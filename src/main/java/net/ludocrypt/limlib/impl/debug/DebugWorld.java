package net.ludocrypt.limlib.impl.debug;

import java.util.Map;

import com.mojang.serialization.Lifecycle;

import net.ludocrypt.limlib.api.LimlibRegistrar;
import net.ludocrypt.limlib.api.LimlibRegistryHooks;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class DebugWorld implements LimlibRegistrar {

	public static final ResourceKey<WorldPreset> DEBUG_KEY = ResourceKey
		.create(Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath("limlib", "debug_nbt"));

	@Override
	public void registerHooks() {
		LimlibRegistryHooks
	 		.hook(Registries.WORLD_PRESET, (LimlibRegistryHooks.LimlibRegistryHook<WorldPreset>) (infoLookup, registryKey, registry) -> registry.register(DEBUG_KEY, new WorldPreset(Map
						 .of(LevelStem.OVERWORLD,
							 new LevelStem(
								 infoLookup
									 .lookup(Registries.DIMENSION_TYPE)
									 .get()
									 .getter()
									 .getOrThrow(BuiltinDimensionTypes.OVERWORLD),
								 new DebugNbtChunkGenerator(
									 infoLookup.lookup(Registries.BIOME).get().getter().getOrThrow(Biomes.THE_VOID))))),
					 RegistrationInfo.BUILT_IN));
	}

}
