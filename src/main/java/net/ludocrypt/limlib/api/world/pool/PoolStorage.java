package net.ludocrypt.limlib.api.world.pool;

import net.ludocrypt.limlib.api.data.storage.DataStorage;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;

public class PoolStorage extends DataStorage<PiecePool> {
	private static final String POOL_FOLDER = "worldgen/piece_pools";

	public PoolStorage() {
		super(PiecePool.CODEC, Limlib.id("limlib_piece_pool_listener"), "worldgen/piece_pools");
	}

	@Override
	protected void insertData(PiecePool pool, ResourceLocation fileId, Map<ResourceLocation, PiecePool> map) {
		if (map.containsKey(pool.getPool()) && !pool.shouldOverride) {
			ResourceLocation id = pool.getPool();
			for (String subPool : pool.getSubPools().keySet()) {
				if (map.get(id).hasSubPool(subPool)) {
					map.get(id).addPiecesToSubPool(subPool, pool.getSubPools().get(subPool).toArray(String[]::new));
				} else {
					map.get(id).addSubPool(subPool, pool.getSubPools().get(subPool).toArray(String[]::new));
				}
			}
		} else {
			map.put(pool.getPool(), pool);
		}
	}
}
