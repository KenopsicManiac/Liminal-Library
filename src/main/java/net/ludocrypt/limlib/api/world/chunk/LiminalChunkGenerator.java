package net.ludocrypt.limlib.api.world.chunk;

import com.mojang.datafixers.util.Either;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public abstract class LiminalChunkGenerator extends ChunkGenerator {

	public LiminalChunkGenerator(BiomeSource biomeSource) {
		super(biomeSource, (biome) -> BiomeGenerationSettings.EMPTY);
	}

	@Override
	public void applyCarvers(WorldGenRegion chunkRegion, long seed, RandomState randomState, BiomeManager biomeAccess,
					  StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving generationStep) {
	}

	@Override
	public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
	}

	@Override
	public void spawnOriginalMobs(WorldGenRegion region) {
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState,
														StructureManager structureManager, ChunkAccess chunk) {
		throw new UnsupportedOperationException("Vanilla populateNoise should never be called in LiminalChunkGenerator");
	}

	/**
	 * The number of neighboring chunks which can be accessed for block placement. A
	 * value of 0 means that only this chunk is accessible. A positive value means
	 * that the given amount of neighbors are accessible in each direction. A
	 * negative value means that this region shouldn't be used for block placement.
	 */
	public abstract int getPlacementRadius();

	/**
	 * An extention of the base populateNoise method but with more variables. Use
	 * ChunkRegion as opposed to world when setting blocks, as it allows you to
	 * extend through multiple chunks in {@link getPlacementRadius} away.
	 */
	public abstract CompletableFuture<ChunkAccess> populateNoise(WorldGenRegion chunkRegion, ChunkStatus targetStatus,
																 Executor executor, ServerLevel world, ChunkGenerator generator,
																 StructureTemplateManager structureTemplateManager, ThreadedLevelLightEngine lightingProvider,
																 Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> fullChunkConverter, List<ChunkAccess> chunks,
																 ChunkAccess chunk);

	@Override
	public int getSeaLevel() {
		return 0;
	}

	@Override
	public int getMinY() {
		return 0;
	}

	@Override
	public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState randomState) {
		return this.getGenDepth();
	}

	@Override
	public NoiseColumn getBaseColumn(int x, int y, LevelHeightAccessor world, RandomState random) {
		BlockState[] states = new BlockState[world.getHeight()];

		for (int i = 0; i < states.length; i++) {
			states[i] = Blocks.AIR.defaultBlockState();
		}

		return new NoiseColumn(0, states);
	}

}
