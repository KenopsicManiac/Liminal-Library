package net.ludocrypt.limlib.impl;

import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.core.RegistryAccess;

public class SaveStorageSupplier {

//	@ModInternal
	public static final AtomicReference<RegistryAccess.Frozen> LOADED_REGISTRY = new AtomicReference<RegistryAccess.Frozen>();

}
