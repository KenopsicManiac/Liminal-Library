package org.dimdev.limlib.api.skybox;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record TexturedSkybox(ResourceLocation identifier) implements Skybox {

	public static final MapCodec<TexturedSkybox> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(ResourceLocation.CODEC.fieldOf("skybox").stable().forGetter(TexturedSkybox::identifier)).apply(instance, instance.stable(TexturedSkybox::new)));

	@Override
	public SkyBoxType<TexturedSkybox> type() {
		return SkyBoxType.TEXTURED;
	}
}
