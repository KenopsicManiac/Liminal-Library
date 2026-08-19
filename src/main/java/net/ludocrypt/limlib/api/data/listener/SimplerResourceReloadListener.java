package net.ludocrypt.limlib.api.data.listener;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * {@code "Wow, these resource listeners are so simple, it'd be crazy if there was a more simpler- OH MY DAYS"}
 * An oversimplified reloadable resource-listener, major caveat being that it applies a map object that uses
 * resource-locations as a key, with {@code T} representing the object-type of choice to bind the keys to.
 */
public abstract class SimplerResourceReloadListener<T> implements SimpleResourceReloadListener<Map<ResourceLocation, T>> {
	protected ResourceLocation listenerId;
	private final String dataFolder;

	public SimplerResourceReloadListener(ResourceLocation listenerId, String dataFolder) {
		this.listenerId = listenerId;
		this.dataFolder = dataFolder;
	}

	@Override
	public CompletableFuture<Map<ResourceLocation, T>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			HashMap<ResourceLocation, T> map = new HashMap<>();

			for (var resource : manager.listResources(dataFolder, id
				-> id.getPath().endsWith(".json")).entrySet()) {
				try (var inputStream = resource.getValue().open()) {
					String namespace = resource.getKey().getNamespace();
					ResourceLocation fileId = ResourceLocation.fromNamespaceAndPath(namespace, resource.getKey().getPath().replace(".json", ""));
					var json = Limlib.GSON.fromJson(new InputStreamReader(inputStream), JsonObject.class);

					T data = provideCodec().parse(JsonOps.INSTANCE, json).getOrThrow();
					insertDataToMap(data, fileId, map);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return Map.copyOf(map);
		}, executor);
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, T> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> loadData(data));
	}

	protected abstract void insertDataToMap(T data, ResourceLocation fileId, Map<ResourceLocation, T> incompleteMap);

	public abstract void loadData(Map<ResourceLocation, T> map);

	/**
	 * A codec for {@code T} is required during the load step of resource-listening.
	 * @return The codec representing {@code T}
	 */
	protected abstract Codec<T> provideCodec();

	@Override
	public ResourceLocation getFabricId() {
		return this.listenerId;
	}
}
