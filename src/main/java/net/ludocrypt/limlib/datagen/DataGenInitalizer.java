package net.ludocrypt.limlib.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.ludocrypt.limlib.api.LimLibRegistries;
import net.ludocrypt.limlib.api.effects.sky.StaticDimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.effects.sound.distortion.StaticDistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.StaticReverbEffect;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.api.skybox.TexturedSkybox;
import net.ludocrypt.limlib.impl.Limlib;
import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DataGenInitalizer implements DataGeneratorEntrypoint {
	public static final ResourceKey<WorldPreset> DEBUG_KEY = ResourceKey.create(Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath("limlib", "debug_nbt"));

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

		var pack = fabricDataGenerator.createPack();
		pack.addProvider(new FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>() {
			@Override
			public DataProvider create(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
				return new FabricDynamicRegistryProvider(output, registriesFuture) {
					@Override
					protected void configure(HolderLookup.Provider registries, Entries entries) {
						entries.add(ResourceKey.create(LimLibRegistries.SKYBOX, ResourceLocation.withDefaultNamespace("overworld")), new TexturedSkybox(Limlib.id("textures/sky/yearning_canal")));
						entries.add(ResourceKey.create(LimLibRegistries.DIMENSION_EFFECTS, ResourceLocation.withDefaultNamespace("overworld")), StaticDimensionEffects.EMPTY);

						entries.add(ResourceKey.create(SoundEffects.SOUND_EFFECTS_KEY, ResourceLocation.withDefaultNamespace("overworld")), new SoundEffects(Optional.of(new StaticReverbEffect.Builder().setDecayTime(20.0F).build()),
							Optional.empty(), Optional.empty()));

						registries.lookup(Registries.BIOME).flatMap(a -> a.get(Biomes.THE_VOID)).ifPresent(biome -> {
							registries.lookup(Registries.DIMENSION_TYPE).flatMap(b -> b.get(BuiltinDimensionTypes.OVERWORLD)).ifPresent(dimension -> {
								entries.add(DEBUG_KEY, new WorldPreset(
									Map.of(LevelStem.OVERWORLD, new LevelStem(
										dimension, new DebugNbtChunkGenerator(biome)
									))
								));
							});
						});
					}

					@Override
					public String getName() {
						return "Liminal Test";
					}
				};
			}
		});

		pack.addProvider(new FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider>() {
			@Override
			public DataProvider create(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
				return new FabricTagProvider<WorldPreset>(output, Registries.WORLD_PRESET, registriesFuture) {
					@Override
					protected void addTags(HolderLookup.Provider wrapperLookup) {
						this.tag(WorldPresetTags.EXTENDED).addOptional(DEBUG_KEY.location());
					}
				};
			}
		});
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {

	}
}
