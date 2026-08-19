package net.ludocrypt.limlib.api.data.storage;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public abstract class ResourceStorage<T> extends AbstractStorage<T> {
	public ResourceStorage(Codec<T> dataCodec, ResourceLocation listenerId, String dataFolder) {
		super(dataCodec, listenerId, dataFolder, PackType.CLIENT_RESOURCES);
	}
}
