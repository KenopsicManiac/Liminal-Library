package org.dimdev.limlib.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dimdev.limlib.api.LimLibRegistires;
import org.dimdev.limlib.post.PostEffect;
import org.dimdev.limlib.api.effects.sky.DimensionEffects;
import org.dimdev.limlib.api.effects.sound.distortion.DistortionEffect;
import org.dimdev.limlib.api.effects.sound.reverb.ReverbEffect;
import org.dimdev.limlib.api.skybox.Skybox;
import org.dimdev.limlib.api.world.pool.LimlibPoolApi;
import org.dimdev.limlib.impl.debug.DebugDynamicChunkGenerator;
import org.dimdev.limlib.impl.debug.DebugNbtChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.api.ISided;
import org.dimdev.limlib.api.ModCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Limlib implements ModCommon<ISided<?>> {
	INSTANCE;

	public static final Logger LOGGER = LoggerFactory.getLogger("Limlib");
	public static final Gson GSON = new GsonBuilder().create();
	private static ISided<?> sided;

	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath("limlib", id);
	}

	public static ISided<?> getSided() {
		return sided;
	}

	@Override
	public void init(ISided<?> sided) {
		Limlib.sided = sided;

		LimLibRegistires.register();
		ReverbEffect.ReverbEffectType.register();
		DistortionEffect.DistortionEffectType.register();
		DimensionEffects.DimensionEffectsType.register();
		PostEffect.PostEffectType.register();
		Skybox.SkyBoxType.register();

		LimlibPoolApi.initialize();

		sided.registerRunnable(Registries.CHUNK_GENERATOR, () -> {
			sided.registerChunkGenerator("debug_nbt_chunk_generator", DebugNbtChunkGenerator.CODEC);
			sided.registerChunkGenerator("debug_dynamic_chunk_generator", DebugDynamicChunkGenerator.CODEC);
		});
	}

	@Override
	public String getModId() {
		return "limlib";
	}
}
