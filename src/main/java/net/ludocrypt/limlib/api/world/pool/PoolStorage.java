package net.ludocrypt.limlib.api.world.pool;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PoolStorage {
	private static Map<ResourceLocation, PiecePool> POOLS = new HashMap<>();

	private static void loadPoolData(Map<ResourceLocation, PiecePool> newPoolMap) {
		POOLS = newPoolMap;
	}

	public static PiecePool getPool(ResourceLocation id) throws NoSuchElementException {
		if (!POOLS.containsKey(id)) Limlib.LOGGER.error("This pool does not exist: {}", id, new NoSuchElementException());
		return POOLS.get(id);
	}

	private static class PoolListener implements SimpleResourceReloadListener<Map<ResourceLocation, PiecePool>> {
		@Override
		public CompletableFuture<Map<ResourceLocation, PiecePool>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
			return CompletableFuture.supplyAsync(() -> {
				HashMap<ResourceLocation, PiecePool> map = new HashMap<>();

				for (var resource : manager.listResources("worldgen/piece_pools", id
					-> id.getPath().endsWith(".json")).entrySet()) {
					try (var inputStream = resource.getValue().open()) {
						var json = Limlib.GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);

						PiecePool pool = PiecePool.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
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
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return Map.copyOf(map);
			}, executor);
		}

		@Override
		public CompletableFuture<Void> apply(Map<ResourceLocation, PiecePool> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
			return CompletableFuture.runAsync(() -> loadPoolData(data), executor);
		}

		@Override
		public ResourceLocation getFabricId() {
			return Limlib.id("pool_listener");
		}
	}

	public static void initializePoolStorage() {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new PoolListener());
	}
}
