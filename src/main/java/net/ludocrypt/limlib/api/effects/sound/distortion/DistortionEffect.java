package net.ludocrypt.limlib.api.effects.sound.distortion;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.ludocrypt.limlib.api.LimLibRegistries;
import net.ludocrypt.limlib.api.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;

/**
 * A Distortion effect controls
 */
public interface DistortionEffect {
	MappedRegistry<MapCodec<? extends DistortionEffect>> REGISTRY = Utils.createRegistry(LimLibRegistries.DISTORTION_EFFECT_CODEC);
	Codec<DistortionEffect> CODEC = REGISTRY.byNameCodec().dispatchStable(DistortionEffect::getCodec, Function.identity());

	static void init() {
		Utils.register(REGISTRY, "static", StaticDistortionEffect.CODEC);
		DynamicRegistries.register(LimLibRegistries.DISTORTION_EFFECT, DistortionEffect.CODEC);
	}

	MapCodec<? extends DistortionEffect> getCodec();

	/**
	 * Whether or not a Sound Event should be ignored
	 *
	 * @param identifier the Identifier of the Sound Event
	 */
	boolean shouldIgnore(ResourceLocation identifier);

	boolean isEnabled(Minecraft client, SoundInstance soundInstance);

	float getEdge(Minecraft client, SoundInstance soundInstance);

	float getGain(Minecraft client, SoundInstance soundInstance);

	float getLowpassCutoff(Minecraft client, SoundInstance soundInstance);

	float getEQCenter(Minecraft client, SoundInstance soundInstance);

	float getEQBandWidth(Minecraft client, SoundInstance soundInstance);
}
