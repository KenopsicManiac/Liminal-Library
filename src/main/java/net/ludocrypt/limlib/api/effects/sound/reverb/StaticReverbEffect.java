package net.ludocrypt.limlib.api.effects.sound.reverb;

import com.mojang.serialization.MapCodec;
import net.ludocrypt.limlib.api.Utils;
import org.lwjgl.openal.EXTEfx;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;

/**
 * A Reverb effect controls
 * <p>
 * This is a simplification of the base {@link ReverbEffect} class, where each
 * setting is a static, non-changing value
 */
public record StaticReverbEffect(boolean enabled, float density, float diffusion, float gain, float gainHF,
								 float decayTime, float decayHFRatio, float airAbsorptionGainHF,
								 float reflectionsGainBase, float lateReverbGainBase, float reflectionsDelay,
								 float lateReverbDelay, int decayHFLimit) implements ReverbEffect {

	public static final MapCodec<StaticReverbEffect> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
		return instance.group(Codec.BOOL.optionalFieldOf("enabled", true).stable().forGetter(StaticReverbEffect::enabled),
				Utils.floatRangeCodec("density", EXTEfx.AL_REVERB_MIN_DENSITY, EXTEfx.AL_REVERB_MAX_DENSITY, EXTEfx.AL_REVERB_DEFAULT_DENSITY, StaticReverbEffect::density),
				Utils.floatRangeCodec("diffusion", EXTEfx.AL_REVERB_MIN_DIFFUSION, EXTEfx.AL_REVERB_MAX_DIFFUSION, EXTEfx.AL_REVERB_DEFAULT_DIFFUSION, StaticReverbEffect::diffusion),
				Utils.floatRangeCodec("gain", EXTEfx.AL_REVERB_MIN_GAIN, EXTEfx.AL_REVERB_MAX_GAIN, EXTEfx.AL_REVERB_DEFAULT_GAIN, StaticReverbEffect::gain),
				Utils.floatRangeCodec("gain_hf", EXTEfx.AL_REVERB_MIN_GAINHF, EXTEfx.AL_REVERB_MAX_GAINHF, EXTEfx.AL_REVERB_DEFAULT_GAINHF, StaticReverbEffect::gainHF),
				Utils.floatRangeCodec("decay_time", EXTEfx.AL_REVERB_MIN_DECAY_TIME, EXTEfx.AL_REVERB_MAX_DECAY_TIME, EXTEfx.AL_REVERB_DEFAULT_DECAY_TIME, StaticReverbEffect::decayTime),
				Utils.floatRangeCodec("decay_hf_ratio", EXTEfx.AL_REVERB_MIN_DECAY_HFRATIO, EXTEfx.AL_REVERB_MAX_DECAY_HFRATIO, EXTEfx.AL_REVERB_DEFAULT_DECAY_HFRATIO, StaticReverbEffect::decayHFRatio),
				Utils.floatRangeCodec("air_absorption_gain_hf", EXTEfx.AL_REVERB_MIN_AIR_ABSORPTION_GAINHF, EXTEfx.AL_REVERB_MAX_AIR_ABSORPTION_GAINHF, EXTEfx.AL_REVERB_DEFAULT_AIR_ABSORPTION_GAINHF, StaticReverbEffect::airAbsorptionGainHF),
				Utils.floatRangeCodec("max_reflections_gain", EXTEfx.AL_REVERB_MIN_REFLECTIONS_GAIN, EXTEfx.AL_REVERB_MAX_REFLECTIONS_GAIN, EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_GAIN, StaticReverbEffect::reflectionsGainBase),
				Utils.floatRangeCodec("late_reverb_gain", EXTEfx.AL_REVERB_MIN_LATE_REVERB_GAIN, EXTEfx.AL_REVERB_MAX_LATE_REVERB_GAIN, EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_GAIN, StaticReverbEffect::lateReverbGainBase),
				Utils.floatRangeCodec("reflections_delay", EXTEfx.AL_REVERB_MIN_REFLECTIONS_DELAY, EXTEfx.AL_REVERB_MAX_REFLECTIONS_DELAY, EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_DELAY, StaticReverbEffect::reflectionsDelay),
				Utils.floatRangeCodec("late_reverb_delay", EXTEfx.AL_REVERB_MIN_LATE_REVERB_DELAY, EXTEfx.AL_REVERB_MAX_LATE_REVERB_DELAY, EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_DELAY, StaticReverbEffect::lateReverbDelay),
				Codec.intRange(EXTEfx.AL_REVERB_MIN_DECAY_HFLIMIT, EXTEfx.AL_REVERB_MAX_DECAY_HFLIMIT).optionalFieldOf("decay_hf_limit", EXTEfx.AL_REVERB_DEFAULT_DECAY_HFLIMIT).stable().forGetter(StaticReverbEffect::decayHFLimit))
			.apply(instance, instance.stable(StaticReverbEffect::new));
	});

	@Override
	public MapCodec<? extends ReverbEffect> getCodec() {
		return CODEC;
	}

	@Override
	public boolean shouldIgnore(ResourceLocation identifier) {
		return identifier.getPath().contains("ui.") || identifier.getPath().contains("music.") || identifier
			.getPath()
			.contains("block.lava.pop") || identifier.getPath().contains("weather.") || identifier
			.getPath()
			.startsWith("atmosfera") || identifier.getPath().startsWith("dynmus");
	}

	@Override
	public boolean isEnabled(Minecraft client, SoundInstance soundInstance) {
		return this.enabled;
	}

	@Override
	public float getAirAbsorptionGainHF(Minecraft client, SoundInstance soundInstance) {
		return this.airAbsorptionGainHF;
	}

	@Override
	public float getDecayHFRatio(Minecraft client, SoundInstance soundInstance) {
		return this.decayHFRatio;
	}

	@Override
	public float getDensity(Minecraft client, SoundInstance soundInstance) {
		return this.density;
	}

	@Override
	public float getDiffusion(Minecraft client, SoundInstance soundInstance) {
		return this.diffusion;
	}

	@Override
	public float getGain(Minecraft client, SoundInstance soundInstance) {
		return this.gain;
	}

	@Override
	public float getGainHF(Minecraft client, SoundInstance soundInstance) {
		return this.gainHF;
	}

	@Override
	public float getLateReverbGainBase(Minecraft client, SoundInstance soundInstance) {
		return this.lateReverbGainBase;
	}

	@Override
	public float getDecayTime(Minecraft client, SoundInstance soundInstance) {
		return this.decayTime;
	}

	@Override
	public float getReflectionsGainBase(Minecraft client, SoundInstance soundInstance) {
		return this.reflectionsGainBase;
	}

	@Override
	public int getDecayHFLimit(Minecraft client, SoundInstance soundInstance) {
		return this.decayHFLimit;
	}

	@Override
	public float getLateReverbDelay(Minecraft client, SoundInstance soundInstance) {
		return this.lateReverbDelay;
	}

	@Override
	public float getReflectionsDelay(Minecraft client, SoundInstance soundInstance) {
		return this.reflectionsDelay;
	}

	public static class Builder {

		private boolean enabled = true;
		private float density = EXTEfx.AL_REVERB_DEFAULT_DENSITY;
		private float diffusion = EXTEfx.AL_REVERB_DEFAULT_DIFFUSION;
		private float gain = EXTEfx.AL_REVERB_DEFAULT_GAIN;
		private float gainHF = EXTEfx.AL_REVERB_DEFAULT_GAINHF;
		private float decayTime = EXTEfx.AL_REVERB_DEFAULT_DECAY_TIME;
		private float decayHFRatio = EXTEfx.AL_REVERB_DEFAULT_DECAY_HFRATIO;
		private float airAbsorptionGainHF = EXTEfx.AL_REVERB_DEFAULT_AIR_ABSORPTION_GAINHF;
		private float reflectionsGainBase = EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_GAIN;
		private float lateReverbGainBase = EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_GAIN;
		private float reflectionsDelay = EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_DELAY;
		private float lateReverbDelay = EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_DELAY;
		private int decayHFLimit = EXTEfx.AL_REVERB_DEFAULT_DECAY_HFLIMIT;

		public Builder setAirAbsorptionGainHF(float airAbsorptionGainHF) {
			this.airAbsorptionGainHF = airAbsorptionGainHF;
			return this;
		}

		public Builder setDecayHFRatio(float decayHFRatio) {
			this.decayHFRatio = decayHFRatio;
			return this;
		}

		public Builder setDensity(float density) {
			this.density = density;
			return this;
		}

		public Builder setDiffusion(float diffusion) {
			this.diffusion = diffusion;
			return this;
		}

		public Builder setEnabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public Builder setGain(float gain) {
			this.gain = gain;
			return this;
		}

		public Builder setGainHF(float gainHF) {
			this.gainHF = gainHF;
			return this;
		}

		public Builder setLateReverbGainBase(float lateReverbGainBase) {
			this.lateReverbGainBase = lateReverbGainBase;
			return this;
		}

		public Builder setDecayTime(float decayTime) {
			this.decayTime = decayTime;
			return this;
		}

		public Builder setReflectionsGainBase(float reflectionsGainBase) {
			this.reflectionsGainBase = reflectionsGainBase;
			return this;
		}

		public Builder setDecayHFLimit(int decayHFLimit) {
			this.decayHFLimit = decayHFLimit;
			return this;
		}

		public Builder setLateReverbDelay(float lateReverbDelay) {
			this.lateReverbDelay = lateReverbDelay;
			return this;
		}

		public Builder setReflectionsDelay(float reflectionsDelay) {
			this.reflectionsDelay = reflectionsDelay;
			return this;
		}

		public StaticReverbEffect build() {
			return new StaticReverbEffect(this.enabled, this.density, this.diffusion, this.gain, this.gainHF, this.decayTime,
				this.decayHFRatio, this.airAbsorptionGainHF, this.reflectionsGainBase, this.lateReverbGainBase,
				this.reflectionsDelay, this.lateReverbDelay, this.decayHFLimit);
		}

	}

}
