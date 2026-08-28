package net.ludocrypt.limlib.api.data.storage;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

/**
 * A superclass to help with simplifying data-storage, intended to replace dynamic registries.
 * You are recommended to make a static instance of your {@code DataStorage} object for easy-access
 */
public abstract class DataStorage<T> extends AbstractStorage<T> {

	public DataStorage(Codec<T> dataCodec, ResourceLocation listenerId, String dataFolder) {
		super(dataCodec, listenerId, dataFolder, PackType.SERVER_DATA);
	}
}
