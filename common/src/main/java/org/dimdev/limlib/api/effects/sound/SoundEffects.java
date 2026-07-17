package org.dimdev.limlib.api.effects.sound;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.dimdev.limlib.api.effects.sound.distortion.DistortionEffect;
import org.dimdev.limlib.api.effects.sound.reverb.ReverbEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;

public record SoundEffects(Optional<ReverbEffect> reverb, Optional<DistortionEffect> distortion, Optional<Music> music) {

	public static final ResourceKey<Registry<SoundEffects>> SOUND_EFFECTS_KEY = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("limlib_sound_effects"));

	public static final Codec<SoundEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ReverbEffect.CODEC.optionalFieldOf("reverb").stable().forGetter(SoundEffects::reverb),
		DistortionEffect.CODEC.optionalFieldOf("distortion").stable().forGetter(SoundEffects::distortion),
		Music.CODEC.optionalFieldOf("music").stable().forGetter(SoundEffects::music)
	).apply(instance, instance.stable(SoundEffects::new)));
}
