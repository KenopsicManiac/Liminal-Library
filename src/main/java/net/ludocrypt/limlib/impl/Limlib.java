package net.ludocrypt.limlib.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ludocrypt.limlib.api.LimlibRegistrar;
import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbEffect;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;

public class Limlib implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("Limlib");

	@Override
	public void onInitialize() {
		LimlibWorld.load();

		// Effects
		ReverbEffect.init();
		DistortionEffect.init();
		DimensionEffects.init();
		PostEffect.init();
		Skybox.init();

		Registry
			.register(BuiltInRegistries.CHUNK_GENERATOR, new ResourceLocation("limlib", "debug_nbt_chunk_generator"),
				DebugNbtChunkGenerator.CODEC);
		FabricLoader.getInstance()
			.getEntrypoints(LimlibRegistrar.ENTRYPOINT_KEY, LimlibRegistrar.class)
			.forEach(LimlibRegistrar::registerHooks);
	}

}
