package net.ludocrypt.limlib.impl.compat.iris;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.ludocrypt.limlib.impl.services.IrisCompatImpl;

public class IrisCompat implements IrisCompatImpl {
	@Override
	public boolean shadersInUse() {
		IrisApi api = IrisApi.getInstance();
		Iris.getPipelineManager()
			.preparePipeline(Iris.getCurrentDimension());

		return api.isShaderPackInUse();
	}
}
