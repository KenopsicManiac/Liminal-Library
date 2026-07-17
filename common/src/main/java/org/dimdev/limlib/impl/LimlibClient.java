package org.dimdev.limlib.impl;

import org.dimdev.limlib.api.client.effect.EffectRenderers;
import org.dimdev.limlib.impl.shader.PostProcesserManager;
import org.dimdev.limlib.api.client.IClientSided;
import org.dimdev.limlib.api.client.ModClient;

public enum LimlibClient implements ModClient<IClientSided<?>> {
	INSTANCE;

	@Override
	public void init(IClientSided<?> sided) {
		EffectRenderers.init();
		sided.registerClientLoader("shaders", PostProcesserManager.INSTANCE::onResourceManagerReload);
	}

	@Override
	public String getModId() {
		return "limlib";
	}
}
