package net.ludocrypt.limlib.api.world.pool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class PiecePool {
	private final ResourceLocation pool;
	protected final HashMap<String, List<String>> subPools;
	public final boolean shouldOverride;

	public static final Codec<PiecePool> CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			ResourceLocation.CODEC.fieldOf("pool").stable().forGetter(PiecePool::getPool),
			Codec.unboundedMap(Codec.STRING, Codec.list(Codec.STRING)).fieldOf("sub_pools").forGetter(PiecePool::getSubPools),
			Codec.BOOL.optionalFieldOf("override", false)
				.stable().forGetter(pool -> pool.shouldOverride)
		).apply(instance, PiecePool::new));

	public PiecePool(ResourceLocation pool, Map<String, List<String>> subPools, boolean shouldOverride) {
		this.pool = pool;
		this.subPools = new HashMap<>(subPools);
		this.shouldOverride = shouldOverride;
	}

	public PiecePool(ResourceLocation pool, Map<String, List<String>> subPools) {
		this(pool, subPools, false);
	}

	public ResourceLocation getPool() {
		return pool;
	}

	public Map<String, List<String>> getSubPools() {
		return Map.copyOf(subPools);
	}

	public boolean hasSubPool(String subPool) {
		return subPools.containsKey(subPool);
	}

	public void addSubPool(String subPool, String... pieces) {
		if (!hasSubPool(subPool)) {
			subPools.put(subPool, List.of(pieces));
		}
	}

	public void addPiecesToSubPool(String subPool, String... pieces) {
		if (hasSubPool(subPool)) {
			List<String> filteredList = Arrays.stream(pieces)
				.filter(piece -> !subPools.get(subPool).contains(piece))
				.toList();
			subPools.get(subPool).addAll(filteredList);
		}
	}

	public void removeSubPool(String subPool) {
		subPools.remove(subPool);
	}

	public void removePiecesFromSubPool(String subPool, String... pieces) {
		if (hasSubPool(subPool)) {
			List<String> filteredList = Arrays.stream(pieces)
				.filter(piece -> subPools.get(subPool).contains(piece))
				.toList();
			subPools.get(subPool).removeAll(filteredList);
		}
	}

	public NbtGroup convertToGroup() {
		NbtGroup.Builder builder = NbtGroup.Builder
			.create(pool);

		for (String subPool : subPools.keySet()) {
			builder = builder.with(subPool, subPools.get(subPool).toArray(String[]::new));
		}

		return builder.build();
	}
}
