package net.ludocrypt.limlib.api.effects;

import net.ludocrypt.limlib.api.effects.sound.SoundEffectStorage;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;

import java.util.Optional;

//TODO Test API on debug world
public class LimlibEffectsApi {
	protected static final SoundEffectStorage SOUND_STORAGE = new SoundEffectStorage();

	public static Optional<SoundEffects> getEffects(ResourceLocation dimensionId) {
		if (!SOUND_STORAGE.hasEntry(dimensionId)) return Optional.empty();

		return Optional.of(SOUND_STORAGE.getEntry(dimensionId));
	}

	public static Optional<DistortionEffect> getDistortion(ResourceLocation dimensionId) {
		return getEffects(dimensionId).flatMap(SoundEffects::distortion);
	}

	public static Optional<ReverbEffect> getReverb(ResourceLocation dimensionId) {
		return getEffects(dimensionId).flatMap(SoundEffects::reverb);
	}

	public static Optional<Music> getMusic(ResourceLocation dimensionId) {
		return getEffects(dimensionId).flatMap(SoundEffects::music);
	}
}
