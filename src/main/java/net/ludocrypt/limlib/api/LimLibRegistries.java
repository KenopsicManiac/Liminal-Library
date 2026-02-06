package net.ludocrypt.limlib.api;

import com.mojang.serialization.MapCodec;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionEffect;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbEffect;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class LimLibRegistries {
	public static final ResourceKey<Registry<MapCodec<? extends PostEffect>>> POST_EFFECT_CODEC = key("codec/post_effect");
	public static final ResourceKey<Registry<PostEffect>> POST_EFFECT = key("post_effect");
	public static final ResourceKey<Registry<MapCodec<? extends DistortionEffect>>> DISTORTION_EFFECT_CODEC = key("codec/distortion_effect");
	public static final ResourceKey<Registry<DistortionEffect>> DISTORTION_EFFECT = key("distortion_effect");
	public static final ResourceKey<Registry<MapCodec<? extends DimensionEffects>>> DIMENSION_EFFECTS_CODEC = key("codec/dimension_effects");
	public static final ResourceKey<Registry<DimensionEffects>> DIMENSION_EFFECTS = key("dimension_effects");
	public static final ResourceKey<Registry<MapCodec<? extends Skybox>>> SKYBOX_CODEC = key("codec/skybox");
	public static final ResourceKey<Registry<Skybox>> SKYBOX = key("skybox");
	public static final ResourceKey<Registry<MapCodec<? extends ReverbEffect>>> REVERB_EFFECT_CODEC = key("codec/reverb_effect");
	public static final ResourceKey<Registry<ReverbEffect>> REVERB_EFFECT = key("reverb_effect");

	private static <T> ResourceKey<Registry<T>> key(String name) {
		return ResourceKey.createRegistryKey(Limlib.id(name));
	}
}
