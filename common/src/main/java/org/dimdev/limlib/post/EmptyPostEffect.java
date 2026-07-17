package org.dimdev.limlib.post;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class EmptyPostEffect implements PostEffect {

	public static final MapCodec<EmptyPostEffect> CODEC = RecordCodecBuilder
		.mapCodec((instance) -> instance.stable(new EmptyPostEffect()));

	@Override
	public PostEffectType<EmptyPostEffect> type() {
		return PostEffectType.EMPTY;
	}

	@Override
	public boolean shouldRender() {
		return false;
	}


	@Override
	public ResourceLocation getShaderLocation() {
		return null;
	}

}
