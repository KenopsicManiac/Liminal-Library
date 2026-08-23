package net.ludocrypt.limlib.api.effects.sound;

import net.ludocrypt.limlib.api.data.storage.ResourceStorage;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class SoundEffectStorage extends ResourceStorage<SoundEffects> {
	public SoundEffectStorage() {
		super(SoundEffects.CODEC, Limlib.id("sound_effect_listener"), "dim_sound_effects");
	}

	@Override
	protected void insertData(SoundEffects data, ResourceLocation fileId, Map<ResourceLocation, SoundEffects> incompleteMap) {
		for (ResourceLocation dimensionId : data.dimensionIds()) {
			if (!incompleteMap.containsKey(dimensionId)) {
				incompleteMap.put(dimensionId, data);
			}
		}
	}
}
