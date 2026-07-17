package org.dimdev.limlib.api.world.pool;

import org.dimdev.limlib.api.world.NbtGroup;
import net.minecraft.resources.ResourceLocation;

public class LimlibPoolApi {
	public static PiecePool getPool(ResourceLocation id) {
		return PoolStorage.getPool(id);
	}

	public static NbtGroup getPoolAsGroup(ResourceLocation id) {
		return getPool(id).convertToGroup();
	}

	public static void initialize() {
		PoolStorage.initializePoolStorage();
	}
}
