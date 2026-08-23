package net.ludocrypt.limlib.impl;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.ludocrypt.limlib.api.effects.LimlibEffectsApi;
import net.ludocrypt.limlib.impl.shader.PostProcesserManager;
import net.minecraft.server.packs.PackType;

public class LimlibClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(PostProcesserManager.INSTANCE);
		initializeClientApis();
	}

	public static void initializeClientApis() {
		LimlibEffectsApi.initializeApi();
	}
}
