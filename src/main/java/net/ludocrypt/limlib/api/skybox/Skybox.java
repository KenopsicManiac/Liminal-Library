package net.ludocrypt.limlib.api.skybox;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.function.Function;

public abstract class Skybox {

	public static final ResourceKey<Registry<MapCodec<? extends Skybox>>> SKYBOX_CODEC_KEY = ResourceKey
		.createRegistryKey(ResourceLocation.parse("limlib/codec/skybox"));
	public static final Registry<MapCodec<? extends Skybox>> SKYBOX_CODEC = RegistriesAccessor
		.callRegisterSimple(SKYBOX_CODEC_KEY, (registry) -> TexturedSkybox.CODEC);
	public static final Codec<Skybox> CODEC = SKYBOX_CODEC. byNameCodec().dispatchStable(Skybox::getCodec, Function.identity());
	public static final ResourceKey<Registry<Skybox>> SKYBOX_KEY = ResourceKey.createRegistryKey(ResourceLocation.parse("limlib/skybox"));

	public abstract MapCodec<? extends Skybox> getCodec();

	@Environment(EnvType.CLIENT)
	public abstract void renderSky(LevelRenderer worldRenderer, Minecraft client, PoseStack matrices,
			Matrix4f projectionMatrix, float tickDelta);

}
