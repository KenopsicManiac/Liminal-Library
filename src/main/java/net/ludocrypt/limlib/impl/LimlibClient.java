package net.ludocrypt.limlib.impl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

import net.ludocrypt.limlib.impl.shader.PostProcesserManager;

public class LimlibClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(PostProcesserManager.INSTANCE);
	}

}
