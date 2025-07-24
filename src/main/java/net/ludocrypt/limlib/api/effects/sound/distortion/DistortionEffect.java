package net.ludocrypt.limlib.api.effects.sound.distortion;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import com.mojang.serialization.MapCodec;
import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * A Distortion effect controls
 */
public abstract class DistortionEffect {

	public static final ResourceKey<Registry<MapCodec<? extends DistortionEffect>>> DISTORTION_EFFECT_CODEC_KEY = ResourceKey
		.createRegistryKey(ResourceLocation.parse("limlib/codec/distortion_effect"));
	public static final Registry<MapCodec<? extends DistortionEffect>> DISTORTION_EFFECT_CODEC = RegistriesAccessor
		.callRegisterSimple(DISTORTION_EFFECT_CODEC_KEY, (registry) -> StaticDistortionEffect.CODEC);
	public static final Codec<DistortionEffect> CODEC = DISTORTION_EFFECT_CODEC
		.byNameCodec()
		.dispatchStable(DistortionEffect::getCodec, Function.identity());

	public abstract MapCodec<? extends DistortionEffect> getCodec();

	/**
	 * Whether or not a Sound Event should be ignored
	 *
	 * @param identifier the Identifier of the Sound Event
	 */
	public abstract boolean shouldIgnore(ResourceLocation identifier);

	public abstract boolean isEnabled(Minecraft client, SoundInstance soundInstance);

	public abstract float getEdge(Minecraft client, SoundInstance soundInstance);

	public abstract float getGain(Minecraft client, SoundInstance soundInstance);

	public abstract float getLowpassCutoff(Minecraft client, SoundInstance soundInstance);

	public abstract float getEQCenter(Minecraft client, SoundInstance soundInstance);

	public abstract float getEQBandWidth(Minecraft client, SoundInstance soundInstance);

}
