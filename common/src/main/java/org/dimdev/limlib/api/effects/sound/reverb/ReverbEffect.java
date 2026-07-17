package org.dimdev.limlib.api.effects.sound.reverb;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import org.dimdev.limlib.api.LimLibRegistires;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.impl.Limlib;

/**
 * A Reverb effect controls
 */
public interface ReverbEffect {
	Codec<ReverbEffect> CODEC = ReverbEffectType.CODEC.dispatch(ReverbEffect::type, ReverbEffectType::codec);

	ReverbEffectType<? extends ReverbEffect> type();

	static void init() {
	}

	/**
	 * Whether or not a Sound Event should be ignored
	 *
	 * @param identifier the Identifier of the Sound Event
	 */
	boolean shouldIgnore(ResourceLocation identifier);
	boolean isEnabled(Minecraft client, SoundInstance soundInstance);
	float getAirAbsorptionGainHF(Minecraft client, SoundInstance soundInstance);
	float getDecayHFRatio(Minecraft client, SoundInstance soundInstance);
	float getDensity(Minecraft client, SoundInstance soundInstance);
	float getDiffusion(Minecraft client, SoundInstance soundInstance);
	float getGain(Minecraft client, SoundInstance soundInstance);
	float getGainHF(Minecraft client, SoundInstance soundInstance);
	float getLateReverbGainBase(Minecraft client, SoundInstance soundInstance);
	float getDecayTime(Minecraft client, SoundInstance soundInstance);
	float getReflectionsGainBase(Minecraft client, SoundInstance soundInstance);
	int getDecayHFLimit(Minecraft client, SoundInstance soundInstance);
	float getLateReverbDelay(Minecraft client, SoundInstance soundInstance);
	float getReflectionsDelay(Minecraft client, SoundInstance soundInstance);

	record ReverbEffectType<T extends ReverbEffect>(MapCodec<T> codec) {
		public static final Codec<ReverbEffectType<?>> CODEC = LimLibRegistires.REVERB_EFFECT_TYPE.byNameCodec();

		public static final ReverbEffectType<StaticReverbEffect> STATIC = register("static", StaticReverbEffect.CODEC);

		public static void register() {}

		static <U extends ReverbEffect> ReverbEffectType<U> register(String id, MapCodec<U> codec) {
			return Limlib.getSided().register(LimLibRegistryKeys.REVERB_EFFECT_TYPE, id, new ReverbEffectType<>(codec));
		}
	}
}
