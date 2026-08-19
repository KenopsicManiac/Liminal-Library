package net.ludocrypt.limlib.api.data.storage;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.ludocrypt.limlib.api.data.listener.SimplerResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;

/**
 * A superclass to help with simplifying data-storage, intended to replace dynamic registries.
 * You are recommended to make a static instance of your {@code DataStorage} object for easy-access
 */
@Experimental
public abstract class DataStorage<T> implements Iterable<Entry<ResourceLocation, T>> {
	private final HashMap<ResourceLocation, T> dataMap = new HashMap<>();
	private final Codec<T> dataCodec;
	private final ResourceLocation listenerId;
	private final String dataFolder;

	public DataStorage(Codec<T> dataCodec, ResourceLocation listenerId, String dataFolder) {
		this.dataCodec = dataCodec;
		this.listenerId = listenerId;
		this.dataFolder = dataFolder;
	}

	public DataStorage(Codec<T> dataCodec, ResourceLocation listenerId, String dataFolder, Map<ResourceLocation, T> baseMap) {
		this(dataCodec, listenerId, dataFolder);
		this.dataMap.putAll(baseMap);
	}

	public T getEntry(ResourceLocation id) {
		return dataMap.get(id);
	}

	public Set<Entry<ResourceLocation, T>> getAllEntries() {
		return dataMap.entrySet();
	}

	public Set<ResourceLocation> getAllIds() {
		Set<ResourceLocation> set = new HashSet<>();
		getAllEntries().iterator()
			.forEachRemaining(entry -> set.add(entry.getKey()));

		return set;
	}

	public Set<T> getAllValues() {
		Set<T> set = new HashSet<>();
		getAllEntries().iterator()
			.forEachRemaining(entry -> set.add(entry.getValue()));

		return set;
	}

	public void addEntry(ResourceLocation id, T value) {
		if (!dataMap.containsKey(id)) dataMap.put(id, value);
	}

	public void removeEntry(ResourceLocation id) {
		dataMap.remove(id);
	}

	protected abstract void insertData(T data, Map<ResourceLocation, T> incompleteMap);

	public void initialize() {
		ResourceManagerHelper.get(PackType.SERVER_DATA)
			.registerReloadListener(
				new SimplerResourceReloadListener<T>(listenerId, dataFolder) {
			@Override
			protected void insertDataToMap(T data, Map<ResourceLocation, T> incompleteMap) {
				insertData(data, incompleteMap);
			}

			@Override
			public void loadData(Map<ResourceLocation, T> map) {
				dataMap.putAll(map);
			}

			@Override
			protected Codec<T> provideCodec() {
				return dataCodec;
			}
		});
	}

	@Override
	public @NotNull Iterator<Entry<ResourceLocation, T>> iterator() {
		return getAllEntries().iterator();
	}
}
