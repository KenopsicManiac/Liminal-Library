package net.ludocrypt.limlib.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.ludocrypt.limlib.api.LimlibRegistrar;
import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.effects.post.EmptyPostEffect;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.post.StaticPostEffect;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.ludocrypt.limlib.api.effects.sky.EmptyDimensionEffects;
import net.ludocrypt.limlib.api.effects.sky.StaticDimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.distortion.StaticDistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.StaticReverbEffect;
import net.ludocrypt.limlib.api.skybox.EmptySkybox;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.api.skybox.TexturedSkybox;
import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Limlib implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("Limlib");

	@Override
	public void onInitialize() {
		LimlibWorld.load();
		Registry.register(ReverbEffect.REVERB_EFFECT_CODEC, ResourceLocation.fromNamespaceAndPath("limlib", "static"), StaticReverbEffect.CODEC);
		Registry.register(DistortionEffect.DISTORTION_EFFECT_CODEC, ResourceLocation.fromNamespaceAndPath("limlib", "static"), StaticDistortionEffect.CODEC);
		Registry.register(DimensionEffects.DIMENSION_EFFECTS_CODEC, ResourceLocation.fromNamespaceAndPath("limlib", "static"), StaticDimensionEffects.CODEC);
		Registry.register(DimensionEffects.DIMENSION_EFFECTS_CODEC, ResourceLocation.fromNamespaceAndPath("limlib", "empty"), EmptyDimensionEffects.CODEC);
		Registry.register(PostEffect.POST_EFFECT_CODEC, ResourceLocation.fromNamespaceAndPath("limlib", "static"), StaticPostEffect.CODEC);
		Registry.register(PostEffect.POST_EFFECT_CODEC, ResourceLocation.fromNamespaceAndPath("limlib", "empty"), EmptyPostEffect.CODEC);
		Registry.register(Skybox.SKYBOX_CODEC, ResourceLocation.fromNamespaceAndPath("limlib", "empty"), EmptySkybox.CODEC);
		Registry.register(Skybox.SKYBOX_CODEC, ResourceLocation.fromNamespaceAndPath("limlib", "textured"), TexturedSkybox.CODEC);
		Registry
			.register(BuiltInRegistries.CHUNK_GENERATOR, ResourceLocation.fromNamespaceAndPath("limlib", "debug_nbt_chunk_generator"),
				DebugNbtChunkGenerator.CODEC);
		FabricLoader.getInstance()
			.getEntrypoints(LimlibRegistrar.ENTRYPOINT_KEY, LimlibRegistrar.class)
			.forEach(LimlibRegistrar::registerHooks);
	}

}
