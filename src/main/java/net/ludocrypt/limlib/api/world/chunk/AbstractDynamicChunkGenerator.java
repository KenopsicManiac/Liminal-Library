package net.ludocrypt.limlib.api.world.chunk;

import net.ludocrypt.limlib.api.world.NbtGroup;
import net.minecraft.world.level.biome.BiomeSource;

public abstract class AbstractDynamicChunkGenerator extends AbstractNbtChunkGenerator implements DynamicNbtUpdater {
	public AbstractDynamicChunkGenerator(BiomeSource biomeSource, NbtGroup defaultNbtGroup) {
		super(biomeSource, defaultNbtGroup);
	}

	/**
	 * Use this method to get your NbtGroup for generating and placing NBTs down
	 * @return Your dynamically-referenced {@link NbtGroup},
	 * or your default one set via the {@link AbstractDynamicChunkGenerator#AbstractDynamicChunkGenerator(BiomeSource, NbtGroup) initializer}
	 */
	@Override
	public NbtGroup getGroup() {
		try {
			return getDynamicGroup();
		} catch (Exception e) {
			return getNbtGroup();
		}
	}

	/**
	 * Responsible for pulling a dynamically-referenced {@link NbtGroup}.
	 * This means that, rather than changing data within the group,
	 * you're changing the group reference itself based on conditionals and where you retrieve it from.
	 * @return a dynamically-referenced {@link NbtGroup}
	 * @see net.ludocrypt.limlib.impl.debug.DebugDynamicChunkGenerator DebugDynamicChunkGenerator for an example implementation
	 */
	public abstract NbtGroup getDynamicGroup();
}
