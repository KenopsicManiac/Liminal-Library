package net.ludocrypt.limlib.api.effects.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;

import java.util.Optional;

public class SoundEffects {

	public static final ResourceKey<Registry<SoundEffects>> SOUND_EFFECTS_KEY = ResourceKey
		.createRegistryKey(new ResourceLocation("limlib/sound_effects"));

	public static final Codec<SoundEffects> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(ReverbEffect.CODEC.optionalFieldOf("reverb").stable().forGetter((soundEffects) -> {
			return soundEffects.reverb;
		}), DistortionEffect.CODEC.optionalFieldOf("distortion").stable().forGetter((soundEffects) -> {
			return soundEffects.distortion;
		}), Music.CODEC.optionalFieldOf("music").stable().forGetter((soundEffects) -> {
			return soundEffects.music;
		})).apply(instance, instance.stable(SoundEffects::new));
	});

	private final Optional<ReverbEffect> reverb;
	private final Optional<DistortionEffect> distortion;
	private final Optional<Music> music;

	public SoundEffects() {
		this(Optional.empty(), Optional.empty(), Optional.empty());
	}

	public SoundEffects(Optional<ReverbEffect> reverb, Optional<DistortionEffect> distortion, Optional<Music> music) {
		this.reverb = reverb;
		this.distortion = distortion;
		this.music = music;
	}

	public Optional<ReverbEffect> getReverb() {
		return reverb;
	}

	public Optional<DistortionEffect> getDistortion() {
		return distortion;
	}

	public Optional<Music> getMusic() {
		return music;
	}

}
