package net.ludocrypt.limlib.api.skybox;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.ludocrypt.limlib.api.LimLibRegistries;
import net.ludocrypt.limlib.api.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.MappedRegistry;
import org.joml.Matrix4f;

import java.util.function.Function;

public interface Skybox {
	MappedRegistry<MapCodec<? extends Skybox>> REGISTRY = Utils.createRegistry(LimLibRegistries.SKYBOX_CODEC);
	Codec<Skybox> CODEC = REGISTRY.byNameCodec().dispatchStable(Skybox::getCodec, Function.identity());

	static void init() {
		Utils.register(REGISTRY, "empty", EmptySkybox.CODEC);
		Utils.register(REGISTRY, "textured", TexturedSkybox.CODEC);
		DynamicRegistries.registerSynced(LimLibRegistries.SKYBOX, Skybox.CODEC);
	}

	MapCodec<? extends Skybox> getCodec();

	@Environment(EnvType.CLIENT)
	void renderSky(LevelRenderer worldRenderer, Minecraft client, PoseStack matrices, Matrix4f projectionMatrix, float tickDelta);
}
