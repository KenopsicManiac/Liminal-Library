package org.dimdev.limlib.api.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import org.dimdev.limlib.api.client.SkyPropertiesCreator;
import org.dimdev.limlib.api.effects.sky.DimensionEffects;
import org.dimdev.limlib.api.skybox.Skybox;
import org.dimdev.limlib.post.PostEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class EffectRenderers {
	private static final Map<Skybox.SkyBoxType<?>, SkyBoxRenderer<?>> SKYBOX_RENDERERS = new HashMap<>();
	private static final Map<PostEffect.PostEffectType<?>, PostEffectRenderer<?>> POST_EFFECT_RENDERERS = new HashMap<>();
	private static final Map<DimensionEffects.DimensionEffectsType<?>, DimensionEffectsCreator<?>> DIMENSION_EFFECTS_CREATORS = new HashMap<>();

	public static <T extends PostEffect> PostEffectRenderer<T> get(T postEffect) {
		var renderer = POST_EFFECT_RENDERERS.get(postEffect.type());

		if(renderer != null) {
			return (PostEffectRenderer<T>) renderer;
		} else {
			return null;
		}
	}

	public static <T extends Skybox> SkyBoxRenderer<T> get(T skybox) {
		var renderer = SKYBOX_RENDERERS.get(skybox.type());

		if(renderer != null) {
			return (SkyBoxRenderer<T>) renderer;
		} else {
			return null;
		}
	}


	public static <T extends DimensionEffects> DimensionSpecialEffects get(T dimensionEffect) {
		var creator = DIMENSION_EFFECTS_CREATORS.get(dimensionEffect.type());

		if(creator != null) {
			return ((DimensionEffectsCreator<T>) creator).getDimensionEffects(dimensionEffect);
		} else {
			return null;
		}
	}

	public interface SkyBoxRenderer<T extends Skybox> {
		void renderSky(T skybox, LevelRenderer worldRenderer, Minecraft client, PoseStack matrices, Matrix4f projectionMatrix, float tickDelta);
	}

	public interface PostEffectRenderer<T extends PostEffect> {
		void beforeRender(PostEffect effect);
	}

	public interface DimensionEffectsCreator<T extends DimensionEffects> {
		DimensionSpecialEffects getDimensionEffects(T t);
	}

	public static <U extends Skybox> void register(Skybox.SkyBoxType<U> type, SkyBoxRenderer<U> renderer) {
		SKYBOX_RENDERERS.putIfAbsent(type, renderer);
	}

	public static <U extends PostEffect> void register(PostEffect.PostEffectType<U> type, PostEffectRenderer<U> renderer) {
		POST_EFFECT_RENDERERS.putIfAbsent(type, renderer);
	}

	public static <U extends DimensionEffects> void register(DimensionEffects.DimensionEffectsType<U> type, DimensionEffectsCreator<U> renderer) {
		DIMENSION_EFFECTS_CREATORS.putIfAbsent(type, renderer);
	}

	public static void init() {
		register(Skybox.SkyBoxType.EMPTY, (skybox, worldRenderer, client, matrices, projectionMatrix, tickDelta) -> {});
		register(Skybox.SkyBoxType.TEXTURED, (skybox, worldRenderer, client, matrices, projectionMatrix, tickDelta) -> {
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

				RenderSystem.setShaderTexture(0, ResourceLocation.parse(skybox.identifier().toString() + "_" + i + ".png"));
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
		});

		register(PostEffect.PostEffectType.EMPTY, effect -> {});
		register(PostEffect.PostEffectType.STATIC, effect -> {});

		register(DimensionEffects.DimensionEffectsType.STATIC, dimensionEffects ->
			SkyPropertiesCreator.create(dimensionEffects.getCloudHeight(), dimensionEffects.alternateSkyColor(), dimensionEffects.skyType(), dimensionEffects.brightenLighting(), dimensionEffects.darkened(),
				dimensionEffects.thickFog()));
	}
}
