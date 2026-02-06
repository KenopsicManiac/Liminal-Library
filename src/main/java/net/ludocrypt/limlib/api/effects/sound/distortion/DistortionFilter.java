package net.ludocrypt.limlib.api.effects.sound.distortion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class DistortionFilter {

	public static final Logger LOGGER = LogManager.getLogger("LimLib | Distortion");

	public static int id = -1;
	public static int slot = -1;

	public static void update() {
		id = EXTEfx.alGenEffects();
		slot = EXTEfx.alGenAuxiliaryEffectSlots();
	}

	public static boolean update(SoundInstance soundInstance, DistortionEffect data) {

		if (id == -1 || slot == -1) {
			update();
		}

		Minecraft client = Minecraft.getInstance();

		if (data.isEnabled(client, soundInstance)) {
			EXTEfx.alAuxiliaryEffectSlotf(slot, EXTEfx.AL_EFFECTSLOT_GAIN, 0);
			EXTEfx.alEffecti(id, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_DISTORTION);
			EXTEfx
				.alEffectf(id, EXTEfx.AL_DISTORTION_EDGE,
					Mth
						.clamp(data.getEdge(client, soundInstance), EXTEfx.AL_DISTORTION_MIN_EDGE,
							EXTEfx.AL_DISTORTION_MAX_EDGE));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_DISTORTION_GAIN,
					Mth
						.clamp(data.getGain(client, soundInstance), EXTEfx.AL_DISTORTION_MIN_GAIN,
							EXTEfx.AL_DISTORTION_MAX_GAIN));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_DISTORTION_LOWPASS_CUTOFF,
					Mth
						.clamp(data.getLowpassCutoff(client, soundInstance), EXTEfx.AL_DISTORTION_MIN_LOWPASS_CUTOFF,
							EXTEfx.AL_DISTORTION_MAX_LOWPASS_CUTOFF));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_DISTORTION_EQCENTER,
					Mth
						.clamp(data.getEQCenter(client, soundInstance), EXTEfx.AL_DISTORTION_MIN_EQCENTER,
							EXTEfx.AL_DISTORTION_MAX_EQCENTER));
			EXTEfx
				.alEffectf(id, EXTEfx.AL_DISTORTION_EQBANDWIDTH,
					Mth
						.clamp(data.getEQBandWidth(client, soundInstance), EXTEfx.AL_DISTORTION_MIN_EQBANDWIDTH,
							EXTEfx.AL_DISTORTION_MAX_EQBANDWIDTH));
			EXTEfx.alAuxiliaryEffectSloti(slot, EXTEfx.AL_EFFECTSLOT_EFFECT, id);
			EXTEfx.alAuxiliaryEffectSlotf(slot, EXTEfx.AL_EFFECTSLOT_GAIN, 1);

			return true;
		}

		return false;
	}

	public static void update(SoundInstance soundInstance, int sourceID) {
		Minecraft client = Minecraft.getInstance();

		if (!(client == null || client.level == null)) {
			Optional<SoundEffects> soundEffects = LookupGrabber
				.snatch(client.level.registryAccess().lookup(SoundEffects.SOUND_EFFECTS_KEY).get(),
					ResourceKey.create(SoundEffects.SOUND_EFFECTS_KEY, client.level.dimension().location()));

			if (soundEffects.isPresent()) {
				Optional<DistortionEffect> distortion = soundEffects.get().distortion();

				if (distortion.isPresent()) {

					if (!distortion.get().shouldIgnore(soundInstance.getLocation())) {

						for (int i = 0; i < 2; i++) {
							AL11.alSourcei(sourceID, EXTEfx.AL_DIRECT_FILTER, 0);
							AL11
								.alSource3i(sourceID, EXTEfx.AL_AUXILIARY_SEND_FILTER,
									update(soundInstance, distortion.get()) ? slot : 0, 0, 0);
							int error = AL11.alGetError();

							if (error == AL11.AL_NO_ERROR) {
								break;
							} else {
								LOGGER.warn("OpenAl Error {}", error);
							}

						}

					}

				}

			}

		}

	}

}
