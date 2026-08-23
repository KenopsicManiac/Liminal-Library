package net.ludocrypt.limlib.api.world.pool;

import net.ludocrypt.limlib.api.world.NbtGroup;
import net.minecraft.resources.ResourceLocation;

public class LimlibPoolApi {
	protected static final PoolStorage POOL_STORAGE = new PoolStorage();

	public static PiecePool getPool(ResourceLocation id) {
		return POOL_STORAGE.getEntry(id);
	}

	public static NbtGroup getPoolAsGroup(ResourceLocation id) {
		return getPool(id).convertToGroup();
	}

	/**
	 * Only call this method if you are shadowing Liminal Library
	 */
	public static void initializeApi() {
		POOL_STORAGE.initialize();
	}
}
