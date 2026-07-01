package net.ludocrypt.limlib.impl.data.collectors;

import com.mojang.serialization.Codec;
import net.ludocrypt.limlib.api.world.NbtGroup;

public interface NbtGroupCollector {
	Codec<NbtGroupCollector> CODEC = CollectorType.REGISTRY
		.byNameCodec()
		.dispatch("type", NbtGroupCollector::getType, CollectorType::collectorCodec);

	NbtGroup collect();

	CollectorType<?> getType();
}
