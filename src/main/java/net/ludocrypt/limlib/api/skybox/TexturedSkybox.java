package net.ludocrypt.limlib.api.skybox;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public record TexturedSkybox(ResourceLocation identifier) implements Skybox {

	public static final MapCodec<TexturedSkybox> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(ResourceLocation.CODEC.fieldOf("skybox").stable().forGetter(TexturedSkybox::identifier)).apply(instance, instance.stable(TexturedSkybox::new)));

	@Override
	@Environment(EnvType.CLIENT)
	public void renderSky(LevelRenderer worldRenderer, Minecraft client, PoseStack matrices,
						  Matrix4f projectionMatrix, float tickDelta) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(Minecraft.useShaderTransparency());

		Vec3 color = client.level.getSkyColor(client.gameRenderer.getMainCamera().getPosition(), tickDelta).scale(255);
		int r = (int) Math.floor(color.x);
		int g = (int) Math.floor(color.y);
		int b = (int) Math.floor(color.z);
		int a = 255;
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

		for (int i = 0; i < 6; ++i) {
			matrices.pushPose();

			if (i == 0) {
				matrices.mulPose(Axis.ZP.rotationDegrees(180.0F));
				matrices.mulPose(Axis.YP.rotationDegrees(180.0F));
			}

			if (i == 1) {
				matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
			}

			if (i == 2) {
				matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
				matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
			}

			if (i == 3) {
				matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
				matrices.mulPose(Axis.ZP.rotationDegrees(180.0F));
			}

			if (i == 4) {
				matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
				matrices.mulPose(Axis.ZP.rotationDegrees(-90.0F));
			}

			Matrix4f matrix4f = matrices.last().pose();

			RenderSystem.setShaderTexture(0, ResourceLocation.parse(identifier.toString() + "_" + i + ".png"));
			BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, 100.0F).setUv(0.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, 100.0F).setUv(1.0F, 1.0F).setColor(r, g, b, a);
			bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, -100.0F).setUv(1.0F, 0.0F).setColor(r, g, b, a);
			BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
			matrices.popPose();
		}

		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
	}

	@Override
	public MapCodec<? extends Skybox> getCodec() {
		return CODEC;
	}
}
