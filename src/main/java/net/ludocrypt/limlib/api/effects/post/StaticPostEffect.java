package net.ludocrypt.limlib.api.effects.post;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record StaticPostEffect(ResourceLocation shaderName) implements PostEffect {

	public static final MapCodec<StaticPostEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("shader_name").stable().forGetter(StaticPostEffect::shaderName)
	).apply(instance, instance.stable(StaticPostEffect::new)));

	@Override
	public MapCodec<StaticPostEffect> getCodec() {
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
		return ResourceLocation.fromNamespaceAndPath(shaderName.getNamespace(), "shaders/post/" + shaderName.getPath() + ".json");
	}

}
