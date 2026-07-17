package org.dimdev.limlib.api;

import org.dimdev.limlib.api.effects.sound.SoundEffects;
import org.dimdev.limlib.post.PostEffect;
import org.dimdev.limlib.api.effects.sky.DimensionEffects;
import org.dimdev.limlib.api.effects.sound.distortion.DistortionEffect;
import org.dimdev.limlib.api.effects.sound.reverb.ReverbEffect;
import org.dimdev.limlib.api.skybox.Skybox;
import org.dimdev.limlib.impl.Limlib;
import net.minecraft.core.Registry;

public class LimLibRegistires {
	public static final Registry<PostEffect.PostEffectType<? extends PostEffect>> POST_EFFECT_TYPE = Limlib.getSided().createRegistry(LimLibRegistryKeys.POST_EFFECT_TYPE);
	public static final Registry<DimensionEffects.DimensionEffectsType<? extends DimensionEffects>> DIMENSION_EFFECTS_TYPE = Limlib.getSided().createRegistry(LimLibRegistryKeys.DIMENSION_EFFECTS_TYPE);
	public static final Registry<DistortionEffect.DistortionEffectType<? extends DistortionEffect>> DISTORTION_EFFECT_TYPE = Limlib.getSided().createRegistry(LimLibRegistryKeys.DISTORTION_EFFECT_TYPE);
	public static final Registry<Skybox.SkyBoxType<? extends Skybox>> SKYBOX_TYPE = Limlib.getSided().createRegistry(LimLibRegistryKeys.SKYBOX_TYPE);
	public static final Registry<ReverbEffect.ReverbEffectType<? extends ReverbEffect>> REVERB_EFFECT_TYPE = Limlib.getSided().createRegistry(LimLibRegistryKeys.REVERB_EFFECT_TYPE);

	public static void register() {
		Limlib.getSided().createDynamicRegistry(LimLibRegistryKeys.POST_EFFECT, PostEffect.CODEC, true);
		Limlib.getSided().createDynamicRegistry(LimLibRegistryKeys.DISTORTION_EFFECT, DistortionEffect.CODEC, true);
		Limlib.getSided().createDynamicRegistry(LimLibRegistryKeys.DIMENSION_EFFECTS, DimensionEffects.CODEC, true);
		Limlib.getSided().createDynamicRegistry(LimLibRegistryKeys.SKYBOX, Skybox.CODEC, true);
		Limlib.getSided().createDynamicRegistry(LimLibRegistryKeys.REVERB_EFFECT, ReverbEffect.CODEC, true);
		Limlib.getSided().createDynamicRegistry(SoundEffects.SOUND_EFFECTS_KEY, SoundEffects.CODEC, true);

	}
}
