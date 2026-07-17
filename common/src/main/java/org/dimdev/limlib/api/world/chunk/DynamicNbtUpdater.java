package org.dimdev.limlib.api.world.chunk;

import org.dimdev.limlib.api.world.FunctionMap;
import org.dimdev.limlib.api.world.NbtGroup;
import org.dimdev.limlib.api.world.NbtPlacerUtil;

public interface DynamicNbtUpdater {
	/**
	 * Tells the updater how to get the NbtGroup for dynamically updating.
	 * You are advised to use {@link AbstractDynamicChunkGenerator} whenever possible,
	 * as it handles most of the boilerplate for this automatically
	 * @return the {@link NbtGroup} intended for nbt generation
	 * @see AbstractDynamicChunkGenerator
	 * @see AbstractNbtChunkGenerator#getStandardNbtGroup()
	 */
	NbtGroup getGroup();

	default void update(AbstractNbtChunkGenerator chunkGenerator) {
		if (chunkGenerator.nbtGroup != getGroup()) {
			chunkGenerator.nbtGroup = getGroup();
			chunkGenerator.structures = new FunctionMap<>(NbtPlacerUtil::load);
			chunkGenerator.nbtGroup.fill(chunkGenerator.structures);
		}
	}
}
