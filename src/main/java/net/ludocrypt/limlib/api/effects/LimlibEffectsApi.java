package net.ludocrypt.limlib.api.effects;

import net.ludocrypt.limlib.api.effects.sound.SoundEffectStorage;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;

import java.util.Optional;

/**
 * Client-sided api for sound effects, such as music, distortion filters and reverb filters
 */
public class LimlibEffectsApi {
	protected static final SoundEffectStorage SOUND_STORAGE = new SoundEffectStorage();

	/**
	 * Gets the sound effects for a dimension
	 * @param dimensionId dimension id
	 * @return if found, the sound effects of the dimension
	 */
	public static Optional<SoundEffects> getEffects(ResourceLocation dimensionId) {
		if (!SOUND_STORAGE.hasEntry(dimensionId)) return Optional.empty();

		return Optional.of(SOUND_STORAGE.getEntry(dimensionId));
	}

	/**
	 * Gets the distortion filter for a dimension
	 * @param dimensionId dimension id
	 * @return if found, the distortion filter of the dimension
	 */
	public static Optional<DistortionEffect> getDistortion(ResourceLocation dimensionId) {
		return getEffects(dimensionId).flatMap(SoundEffects::distortion);
	}

	/**
	 * Gets the reverb filter for a dimension
	 * @param dimensionId dimension id
	 * @return if found, the reverb filter of the dimension
	 */
	public static Optional<ReverbEffect> getReverb(ResourceLocation dimensionId) {
		return getEffects(dimensionId).flatMap(SoundEffects::reverb);
	}

	/**
	 * Gets the music for a dimension
	 * @param dimensionId dimension id
	 * @return if found, the music of the dimension
	 */
	public static Optional<Music> getMusic(ResourceLocation dimensionId) {
		return getEffects(dimensionId).flatMap(SoundEffects::music);
	}

	/**
	 * Only call this method if you are shadowing Liminal Library
	 */
	public static void initializeApi() {
		SOUND_STORAGE.initialize();
	}
}
