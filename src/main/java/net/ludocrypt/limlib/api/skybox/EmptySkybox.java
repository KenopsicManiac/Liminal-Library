package net.ludocrypt.limlib.api.skybox;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class EmptySkybox extends Skybox {

	public static final Codec<EmptySkybox> CODEC = RecordCodecBuilder
		.create((instance) -> instance.stable(new EmptySkybox()));

	@Override
	@Environment(EnvType.CLIENT)
	public void renderSky(LevelRenderer worldRenderer, Minecraft client, PoseStack matrices,
						  Matrix4f projectionMatrix, float tickDelta) {
	}

	@Override
	public Codec<? extends Skybox> getCodec() {
		return CODEC;
	}

}
