package net.ludocrypt.limlib.impl.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import net.ludocrypt.limlib.impl.services.IrisCompatImpl;

public class IrisCompat implements IrisCompatImpl {
	@Override
	public boolean shadersInUse() {
		return IrisApi.getInstance().isShaderPackInUse();
	}

	@Override
	public boolean isRenderingShadowPass() {
		return IrisApi.getInstance().isRenderingShadowPass();
	}
}
