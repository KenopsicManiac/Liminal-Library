package net.ludocrypt.limlib.api.effects.sound;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbEffect;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;

/**
 * Applies effects to sounds in dimensions.
 * JSONs for your sound effects should be put in {@code data/<datapack namespace>/limlib/sound_effects}
 * @param reverb applies a reverb effect to sounds in the dimension
 * @param distortion applies a distortion effect to sounds in the dimension
 * @param music changes the music in the dimension
 */
public record SoundEffects(Optional<ReverbEffect> reverb, Optional<DistortionEffect> distortion, Optional<Music> music) {

	public static final ResourceKey<Registry<SoundEffects>> SOUND_EFFECTS_KEY = ResourceKey.createRegistryKey(Limlib.id("sound_effects"));

	public static final Codec<SoundEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ReverbEffect.CODEC.optionalFieldOf("reverb").stable().forGetter(SoundEffects::reverb),
		DistortionEffect.CODEC.optionalFieldOf("distortion").stable().forGetter(SoundEffects::distortion),
		Music.CODEC.optionalFieldOf("music").stable().forGetter(SoundEffects::music)
	).apply(instance, instance.stable(SoundEffects::new)));
}
