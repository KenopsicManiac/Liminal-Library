package org.dimdev.limlib.impl;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.limlib.api.client.effect.EffectRenderers;
import org.dimdev.limlib.client.specialmodels.SpecialModelLoadingPlugin;
import org.dimdev.limlib.client.specialmodels.SpecialModelShaderRegistry;
import org.dimdev.limlib.impl.client.LimLibClientSided;
import org.dimdev.limlib.impl.shader.PostProcesserManager;
import org.dimdev.limlib.api.client.ModClient;

import java.io.IOException;
import java.util.function.Consumer;

import static org.dimdev.limlib.impl.Limlib.LOGGER;

public enum LimlibClient implements ModClient<LimLibClientSided<?>> {
	INSTANCE;

	@Override
	public void delayedInit() {
		try {
			System.loadLibrary("renderdoc");
		} catch (UnsatisfiedLinkError e) {
			LOGGER.warn("Attempted to use renderdoc without renderdoc installed.");
		}
	}

	@Override
	public void init(LimLibClientSided<?> sided) {
		EffectRenderers.init();
		SpecialModelLoadingPlugin.init(sided);
		sided.registerClientLoader("shaders", PostProcesserManager.INSTANCE::onResourceManagerReload);
	}

	@Override
	public void initShaders(TriConsumer<ResourceLocation, VertexFormat, Consumer<ShaderInstance>> shaderRegister) {
		try {
			SpecialModelShaderRegistry.registerCoreShaders(shaderRegister);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getModId() {
		return "limlib";
	}
}
