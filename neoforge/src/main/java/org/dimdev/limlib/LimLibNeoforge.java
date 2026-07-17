package org.dimdev.limlib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.dimdev.limlib.impl.Limlib;

@Mod("limlib")
public class LimLibNeoforge extends NeoForgeSided<LimLibNeoforge, Limlib> {
	public LimLibNeoforge(IEventBus bus) {
		super(bus, Limlib.INSTANCE);
	}

	@Override
	public String getModId() {
		return "limlib";
	}
}
