package net.ludocrypt.limlib.api.effects.post;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import com.mojang.serialization.MapCodec;
import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public abstract class PostEffect {

	public static final ResourceKey<Registry<MapCodec<? extends PostEffect>>> POST_EFFECT_CODEC_KEY = ResourceKey
		.createRegistryKey(ResourceLocation.parse("limlib/codec/post_effect"));
	public static final Registry<MapCodec<? extends PostEffect>> POST_EFFECT_CODEC = RegistriesAccessor
		.callRegisterSimple(POST_EFFECT_CODEC_KEY, (registry) -> StaticPostEffect.CODEC);
	public static final Codec<PostEffect> CODEC = POST_EFFECT_CODEC
		.byNameCodec()
		.dispatchStable(PostEffect::getCodec, Function.identity());
	public static final ResourceKey<Registry<PostEffect>> POST_EFFECT_KEY = ResourceKey
		.createRegistryKey(ResourceLocation.parse("limlib/post_effect"));

	public abstract MapCodec<? extends PostEffect> getCodec();

	public abstract boolean shouldRender();

	public abstract void beforeRender();

	public abstract ResourceLocation getShaderLocation();

}
