package org.dimdev.limlib.post;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import org.dimdev.limlib.api.LimLibRegistires;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.impl.Limlib;
import net.minecraft.resources.ResourceLocation;

public interface PostEffect {
	Codec<PostEffect> CODEC = PostEffectType.CODEC.dispatch(PostEffect::type, PostEffectType::codec);

	PostEffectType<? extends PostEffect> type();

	boolean shouldRender();

	ResourceLocation getShaderLocation();

	record PostEffectType<T extends PostEffect>(MapCodec<T> codec) {
		public static final Codec<PostEffectType<?>> CODEC = LimLibRegistires.POST_EFFECT_TYPE.byNameCodec();

		public static final PostEffectType<StaticPostEffect> STATIC = register("static", StaticPostEffect.CODEC);
		public static final PostEffectType<EmptyPostEffect> EMPTY = register("empty", EmptyPostEffect.CODEC);

		public static void register() {}

		static <U extends PostEffect> PostEffectType<U> register(String id, MapCodec<U> codec) {
			return Limlib.getSided().register(LimLibRegistryKeys.POST_EFFECT_TYPE, id, new PostEffectType<>(codec));
		}
	}
}
