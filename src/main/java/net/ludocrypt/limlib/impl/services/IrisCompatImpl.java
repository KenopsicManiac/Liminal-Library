package net.ludocrypt.limlib.impl.services;

import org.jetbrains.annotations.Contract;

public interface IrisCompatImpl {
	@Contract(pure = true)
	boolean shadersInUse();
}
