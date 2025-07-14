package net.ludocrypt.limlib.api.effects.post;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public class StaticPostEffect extends PostEffect {

	public static final Codec<StaticPostEffect> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(ResourceLocation.CODEC.fieldOf("shader_name").stable().forGetter((postEffect) -> {
			return postEffect.shaderName;
		})).apply(instance, instance.stable(StaticPostEffect::new));
	});

	private final ResourceLocation shaderName;

	public StaticPostEffect(ResourceLocation shaderLocation) {
		this.shaderName = shaderLocation;
	}

	@Override
	public Codec<? extends PostEffect> getCodec() {
		return CODEC;
	}

	@Override
	public boolean shouldRender() {
		return true;
	}

	@Override
	public void beforeRender() {

	}

	@Override
	public ResourceLocation getShaderLocation() {
		return new ResourceLocation(shaderName.getNamespace(), "shaders/post/" + shaderName.getPath() + ".json");
	}

}
