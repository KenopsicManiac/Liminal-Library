package net.ludocrypt.limlib.api.data.storage;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.ludocrypt.limlib.api.data.listener.SimplerResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;

public abstract class AbstractStorage<T> implements Iterable<Entry<ResourceLocation, T>> {
	private final HashMap<ResourceLocation, T> dataMap = new HashMap<>();
	private final Codec<T> dataCodec;
	private final ResourceLocation listenerId;
	private final String dataFolder;
	private final PackType packType;

	public AbstractStorage(Codec<T> dataCodec, ResourceLocation listenerId, String dataFolder, PackType packType) {
		this.dataCodec = dataCodec;
		this.listenerId = listenerId;
		this.dataFolder = dataFolder;
		this.packType = packType;
	}

	public AbstractStorage(Codec<T> dataCodec, ResourceLocation listenerId, String dataFolder, PackType packType, Map<ResourceLocation, T> baseMap) {
		this(dataCodec, listenerId, dataFolder, packType);
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

	public boolean hasEntry(ResourceLocation id) {
		return dataMap.containsKey(id);
	}

	protected abstract void insertData(T data, ResourceLocation fileId, Map<ResourceLocation, T> incompleteMap);

	public void initialize() {
		ResourceManagerHelper.get(packType)
			.registerReloadListener(
				new SimplerResourceReloadListener<T>(listenerId, dataFolder) {
					@Override
					protected void insertDataToMap(T data, ResourceLocation fileId, Map<ResourceLocation, T> incompleteMap) {
						insertData(data, fileId, incompleteMap);
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
