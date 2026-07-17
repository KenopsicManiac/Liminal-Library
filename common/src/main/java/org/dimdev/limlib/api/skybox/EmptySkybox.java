package org.dimdev.limlib.api.skybox;

import com.mojang.serialization.MapCodec;

public enum EmptySkybox implements Skybox {
	INSTANCE;

	public static final MapCodec<EmptySkybox> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public SkyBoxType<? extends Skybox> type() {
		return SkyBoxType.EMPTY;
	}
}
