package org.dimdev.limlib.api;

import org.dimdev.limlib.post.PostEffect;
import org.dimdev.limlib.api.effects.sky.DimensionEffects;
import org.dimdev.limlib.api.effects.sound.distortion.DistortionEffect;
import org.dimdev.limlib.api.effects.sound.reverb.ReverbEffect;
import org.dimdev.limlib.api.skybox.Skybox;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class LimLibRegistryKeys {
	public static final ResourceKey<Registry<PostEffect.PostEffectType<? extends PostEffect>>> POST_EFFECT_TYPE = key("codec/post_effect");
	public static final ResourceKey<Registry<DimensionEffects.DimensionEffectsType<? extends DimensionEffects>>> DIMENSION_EFFECTS_TYPE = key("codec/dimension_effects");
	public static final ResourceKey<Registry<DistortionEffect.DistortionEffectType<? extends DistortionEffect>>> DISTORTION_EFFECT_TYPE = key("codec/distortion_effect");
	public static final ResourceKey<Registry<Skybox.SkyBoxType<? extends Skybox>>> SKYBOX_TYPE = key("codec/skybox");
	public static final ResourceKey<Registry<ReverbEffect.ReverbEffectType<? extends ReverbEffect>>> REVERB_EFFECT_TYPE = key("codec/reverb_effect");

	public static final ResourceKey<Registry<PostEffect>> POST_EFFECT = key("post_effect");
	public static final ResourceKey<Registry<DistortionEffect>> DISTORTION_EFFECT = key("distortion_effect");
	public static final ResourceKey<Registry<DimensionEffects>> DIMENSION_EFFECTS = key("dimension_effects");
	public static final ResourceKey<Registry<Skybox>> SKYBOX = key("skybox");
	public static final ResourceKey<Registry<ReverbEffect>> REVERB_EFFECT = key("reverb_effect");

	private static <T> ResourceKey<Registry<T>> key(String name) {
		return ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("limlib_" + name));
	}
}
