package net.ludocrypt.limlib.api.skybox;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;

public class EmptySkybox extends Skybox {

	public static final MapCodec<EmptySkybox> CODEC = RecordCodecBuilder
		.mapCodec((instance) -> instance.stable(new EmptySkybox()));

	@Override
	@Environment(EnvType.CLIENT)
	public void renderSky(LevelRenderer worldRenderer, Minecraft client, PoseStack matrices,
			Matrix4f projectionMatrix, float tickDelta) {
	}

	@Override
	public MapCodec<? extends Skybox> getCodec() {
		return CODEC;
	}

}
