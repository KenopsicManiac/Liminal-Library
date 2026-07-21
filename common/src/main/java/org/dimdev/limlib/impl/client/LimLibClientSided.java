package org.dimdev.limlib.impl.client;

import net.minecraft.client.resources.model.BakedModel;
import org.dimdev.limlib.api.client.IClientSided;
import org.jetbrains.annotations.Nullable;

public interface LimLibClientSided<T extends LimLibClientSided<T>> extends IClientSided<T> {
	void registerSpecialModelLoadingPlugin();

	@Nullable
	BakedModel getWrappedBakedModel(BakedModel model);
}
