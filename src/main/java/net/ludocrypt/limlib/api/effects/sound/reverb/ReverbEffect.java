package net.ludocrypt.limlib.api.effects.sound.reverb;

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
 * A Reverb effect controls
 */
public interface ReverbEffect {
	MappedRegistry<MapCodec<? extends ReverbEffect>> REGISTRY = Utils.createRegistry(LimLibRegistries.REVERB_EFFECT_CODEC);
	Codec<ReverbEffect> CODEC = REGISTRY.byNameCodec().dispatchStable(ReverbEffect::getCodec, Function.identity());

	static void init() {
		Utils.register(REGISTRY, "static", StaticReverbEffect.CODEC);
		DynamicRegistries.register(LimLibRegistries.REVERB_EFFECT, ReverbEffect.CODEC);
	}

	public abstract MapCodec<? extends ReverbEffect> getCodec();

	/**
	 * Whether or not a Sound Event should be ignored
	 *
	 * @param identifier the Identifier of the Sound Event
	 */
	public abstract boolean shouldIgnore(ResourceLocation identifier);

	public abstract boolean isEnabled(Minecraft client, SoundInstance soundInstance);

	public abstract float getAirAbsorptionGainHF(Minecraft client, SoundInstance soundInstance);

	public abstract float getDecayHFRatio(Minecraft client, SoundInstance soundInstance);

	public abstract float getDensity(Minecraft client, SoundInstance soundInstance);

	public abstract float getDiffusion(Minecraft client, SoundInstance soundInstance);

	public abstract float getGain(Minecraft client, SoundInstance soundInstance);

	public abstract float getGainHF(Minecraft client, SoundInstance soundInstance);

	public abstract float getLateReverbGainBase(Minecraft client, SoundInstance soundInstance);

	public abstract float getDecayTime(Minecraft client, SoundInstance soundInstance);

	public abstract float getReflectionsGainBase(Minecraft client, SoundInstance soundInstance);

	public abstract int getDecayHFLimit(Minecraft client, SoundInstance soundInstance);

	public abstract float getLateReverbDelay(Minecraft client, SoundInstance soundInstance);

	public abstract float getReflectionsDelay(Minecraft client, SoundInstance soundInstance);

}
