package net.ludocrypt.limlib.impl.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import net.ludocrypt.limlib.impl.services.IrisCompatImpl;
import org.jetbrains.annotations.Contract;

public class IrisCompat implements IrisCompatImpl {
	@Override
	@Contract(pure = true)
	public boolean shadersInUse() {
		return IrisApi.getInstance().isShaderPackInUse();
	}

	@Override
	@Contract(pure = true)
	public boolean isRenderingShadowPass() {
		return IrisApi.getInstance().isRenderingShadowPass();
	}
}
