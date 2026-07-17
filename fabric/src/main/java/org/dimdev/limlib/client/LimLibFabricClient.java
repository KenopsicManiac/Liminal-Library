package org.dimdev.limlib.client;

import org.dimdev.limlib.impl.LimlibClient;

public class LimLibFabricClient extends FabricClientSided<LimLibFabricClient, LimlibClient> {
	public LimLibFabricClient() {
		super(LimlibClient.INSTANCE);
	}
}
