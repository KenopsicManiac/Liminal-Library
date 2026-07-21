package org.dimdev.limlib;

import org.dimdev.limlib.client.FabricClientSided;
import org.dimdev.limlib.client.LimLibFabricClient;
import org.dimdev.limlib.impl.Limlib;
import org.dimdev.limlib.impl.LimlibClient;

public class LimLibFabric extends FabricSided<LimLibFabric, Limlib> {
	public LimLibFabric() {
		super(Limlib.INSTANCE);
	}
}
