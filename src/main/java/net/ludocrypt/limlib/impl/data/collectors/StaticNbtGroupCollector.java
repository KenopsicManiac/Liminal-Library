package net.ludocrypt.limlib.impl.data.collectors;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.world.NbtGroup;

public class StaticNbtGroupCollector implements NbtGroupCollector {
	public static final MapCodec<StaticNbtGroupCollector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NbtGroup.CODEC.fieldOf("group").forGetter(StaticNbtGroupCollector::collect)
	).apply(instance, StaticNbtGroupCollector::new));

	private final NbtGroup group;

	public StaticNbtGroupCollector(NbtGroup group) {
		this.group = group;
	}

	@Override
	public NbtGroup collect() {
		return this.group;
	}

	@Override
	public CollectorType<?> getType() {
		return CollectorTypes.STATIC_GROUP_COLLECTOR;
	}
}
