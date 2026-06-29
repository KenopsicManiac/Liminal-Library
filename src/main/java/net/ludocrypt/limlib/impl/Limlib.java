package net.ludocrypt.limlib.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.ludocrypt.limlib.api.Utils;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbEffect;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.api.world.pool.LimlibPoolApi;
import net.ludocrypt.limlib.impl.debug.DebugDynamicChunkGenerator;
import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Limlib implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("Limlib");
	public static final Gson GSON = new GsonBuilder().create();

	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath("limlib", id);
	}

	@Override
	public void onInitialize() {
		ReverbEffect.init();
		DistortionEffect.init();
		DimensionEffects.init();
		PostEffect.init();
		Skybox.init();
		DynamicRegistries.registerSynced(SoundEffects.SOUND_EFFECTS_KEY, SoundEffects.CODEC);
		LimlibPoolApi.initialize();

		Utils.register(BuiltInRegistries.CHUNK_GENERATOR, "debug_nbt_chunk_generator", DebugNbtChunkGenerator.CODEC);
		Utils.register(BuiltInRegistries.CHUNK_GENERATOR, "debug_dynamic_chunk_generator", DebugDynamicChunkGenerator.CODEC);
	}

}
