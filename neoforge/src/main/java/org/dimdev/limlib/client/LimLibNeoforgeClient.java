package org.dimdev.limlib.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.dimdev.limlib.impl.LimlibClient;

@Mod(value = "limlib", dist = Dist.CLIENT)
public class LimLibNeoforgeClient extends NeoForgeClientSided<LimLibNeoforgeClient, LimlibClient> {
	public LimLibNeoforgeClient(IEventBus bus, ModContainer container) {
		super(bus, container, LimlibClient.INSTANCE);
	}
}
