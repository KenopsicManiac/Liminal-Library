package net.ludocrypt.limlib.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.ludocrypt.limlib.impl.debug.DebugDynamicChunkGenerator;
import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DataGenInitalizer implements DataGeneratorEntrypoint {
	public static final ResourceKey<WorldPreset> DEBUG_KEY = ResourceKey.create(Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath("limlib", "debug_nbt"));
	public static final ResourceKey<WorldPreset> DEBUG_DYNAMIC_KEY = ResourceKey.create(Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath("limlib", "debug_dynamic_nbt"));

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

		var pack = fabricDataGenerator.createPack();
		pack.addProvider((FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>) (output, registriesFuture) -> new FabricDynamicRegistryProvider(output, registriesFuture) {
			@Override
			protected void configure(HolderLookup.Provider registries, Entries entries) {
				registries.lookup(Registries.BIOME).flatMap(a -> a.get(Biomes.THE_VOID)).ifPresent(biome -> {
					registries.lookup(Registries.DIMENSION_TYPE).flatMap(b -> b.get(BuiltinDimensionTypes.OVERWORLD)).ifPresent(dimension -> {
						entries.add(DEBUG_KEY, new WorldPreset(
							Map.of(LevelStem.OVERWORLD, new LevelStem(
								dimension, new DebugNbtChunkGenerator(biome)
							))
						));
						entries.add(DEBUG_DYNAMIC_KEY, new WorldPreset(
							Map.of(LevelStem.OVERWORLD, new LevelStem(
								dimension, new DebugDynamicChunkGenerator(biome)
							))
						));
					});
				});
			}

			@Override
			public String getName() {
				return "Liminal Test";
			}
		});

		pack.addProvider(new FabricDataGenerator.Pack.RegistryDependentFactory<>() {
			@Override
			public DataProvider create(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
				return new FabricTagProvider<WorldPreset>(output, Registries.WORLD_PRESET, registriesFuture) {
					@Override
					protected void addTags(HolderLookup.Provider wrapperLookup) {
						this.tag(WorldPresetTags.EXTENDED).addOptional(DEBUG_KEY.location()).addOptional(DEBUG_DYNAMIC_KEY.location());
					}
				};
			}
		});
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {

	}
}
