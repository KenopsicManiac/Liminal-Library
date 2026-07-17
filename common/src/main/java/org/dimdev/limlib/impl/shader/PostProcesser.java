package org.dimdev.limlib.impl.shader;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import org.dimdev.limlib.impl.Limlib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class PostProcesser {

	private final ResourceLocation location;
	protected PostChain shader;
	private boolean loaded;

	public PostProcesser(ResourceLocation location) {
		this.location = location;
	}

	public void init(ResourceManager resourceManager) {

		try {
			this.release();
			Minecraft client = Minecraft.getInstance();
			this.shader = parseShader(resourceManager, client, this.location);
			this.shader.resize(client.getWindow().getWidth(), client.getWindow().getHeight());
		} catch (IOException e) {
			this.loaded = true;
			Limlib.LOGGER.error("Could not create screen shader {}", this.getLocation(), e);
		}

	}

	protected PostChain parseShader(ResourceManager resourceManager, Minecraft mc, ResourceLocation location)
			throws IOException {
		return new PostChain(mc.getTextureManager(), resourceManager, mc.getMainRenderTarget(), location);
	}

	public void release() {

		if (this.isInitialized()) {

			try {
				assert this.shader != null;
				this.shader.close();
				this.shader = null;
			} catch (Exception e) {
				throw new RuntimeException("Failed to release shader " + this.location, e);
			}

		}

		this.loaded = false;
	}

	public void render(float tickDelta) {
		PostChain shader = this.getShaderEffect();

		if (shader != null) {
			RenderSystem.disableBlend();
			RenderSystem.disableDepthTest();
			RenderSystem.resetTextureMatrix();
			shader.process(tickDelta);
			Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
			RenderSystem.disableBlend();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderSystem.enableDepthTest();
		}

	}

	public ResourceLocation getLocation() {
		return location;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public boolean isInitialized() {
		return this.shader != null;
	}

	public PostChain getShaderEffect() {

		if (!this.isInitialized() && !this.isLoaded()) {
			this.init(Minecraft.getInstance().getResourceManager());
		}

		return this.shader;
	}

}
