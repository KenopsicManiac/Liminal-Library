package org.dimdev.limlib.api.effects.sound.distortion;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import org.dimdev.limlib.api.LimLibRegistires;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.impl.Limlib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;

/**
 * A Distortion effect controls
 */
public interface DistortionEffect {
	Codec<DistortionEffect> CODEC = DistortionEffectType.CODEC.dispatch(DistortionEffect::type, DistortionEffectType::codec);

	DistortionEffectType<? extends DistortionEffect> type();

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

	record DistortionEffectType<T extends DistortionEffect>(MapCodec<T> codec) {
		public static final Codec<DistortionEffect.DistortionEffectType<?>> CODEC = LimLibRegistires.DISTORTION_EFFECT_TYPE.byNameCodec();

		public static final DistortionEffectType<StaticDistortionEffect> STATIC = register("static", StaticDistortionEffect.CODEC);

		public static void register() {}

		static <U extends DistortionEffect> DistortionEffectType<U> register(String id, MapCodec<U> codec) {
			return Limlib.getSided().register(LimLibRegistryKeys.DISTORTION_EFFECT_TYPE, id, new DistortionEffectType<>(codec));
		}
	}
}
