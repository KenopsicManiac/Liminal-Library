package net.ludocrypt.limlib.impl.data.collectors;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.pool.LimlibPoolApi;
import net.minecraft.resources.ResourceLocation;

public record DynamicNbtGroupCollector(ResourceLocation poolId) implements NbtGroupCollector {
	public static final MapCodec<DynamicNbtGroupCollector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("pool_id").forGetter(DynamicNbtGroupCollector::poolId)
	).apply(instance, DynamicNbtGroupCollector::new));

	@Override
	public NbtGroup collect() {
		return LimlibPoolApi.getPoolAsGroup(poolId);
	}

	@Override
	public CollectorType<?> getType() {
		return CollectorTypes.DYNAMIC_GROUP_COLLECTOR;
	}
}
