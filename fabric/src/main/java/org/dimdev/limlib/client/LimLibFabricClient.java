package org.dimdev.limlib.client;

import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import org.dimdev.limlib.client.specialmodels.SpecialModelLoadingPlugin;
import org.dimdev.limlib.impl.LimlibClient;
import org.dimdev.limlib.impl.client.LimLibClientSided;
import org.jetbrains.annotations.Nullable;

public class LimLibFabricClient extends FabricClientSided<LimLibFabricClient, LimlibClient> implements LimLibClientSided<LimLibFabricClient> {
	public LimLibFabricClient() {
		super(LimlibClient.INSTANCE);
	}

	@Override
	public void registerSpecialModelLoadingPlugin() {
		PreparableModelLoadingPlugin.register(SpecialModelLoadingPlugin::loadSpecialModelIds, (specialModelIds, context) -> {
			SpecialModelLoadingPlugin.prepareModelLoading();
			context.addModels(specialModelIds);
		});
	}

	@Override
	public @Nullable BakedModel getWrappedBakedModel(BakedModel model) {
		if (model instanceof WrapperBakedModel wrapper) {
			return wrapper.getWrappedModel();
		}

		return null;
	}
}
