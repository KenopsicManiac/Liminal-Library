package net.ludocrypt.limlib.api.effects.post;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.ludocrypt.limlib.api.LimLibRegistries;
import net.ludocrypt.limlib.api.Utils;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public interface PostEffect {
	Registry<MapCodec<? extends PostEffect>> REGISTRY = Utils.createRegistry(LimLibRegistries.POST_EFFECT_CODEC);
	Codec<PostEffect> CODEC = REGISTRY.byNameCodec().dispatchStable(PostEffect::getCodec, Function.identity());

	static void init() {
		DynamicRegistries.registerSynced(LimLibRegistries.POST_EFFECT, PostEffect.CODEC);
		Utils.register(REGISTRY, "static", StaticPostEffect.CODEC);
		Utils.register(REGISTRY, "empty", EmptyPostEffect.CODEC);
	}

	MapCodec<? extends PostEffect> getCodec();

	boolean shouldRender();

	void beforeRender();

	ResourceLocation getShaderLocation();
}
