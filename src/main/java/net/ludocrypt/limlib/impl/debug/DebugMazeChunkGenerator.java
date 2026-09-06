package net.ludocrypt.limlib.impl.debug;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.*;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Debug chunk generator, used to test maze generators and any changes to them
 * @see net.ludocrypt.limlib.api.world.maze.MazeGenerator MazeGenerator
 */
public class DebugMazeChunkGenerator extends AbstractNbtChunkGenerator {
	// FLINT AND STEEL!
	protected static final NbtGroup GROUP = NbtGroup.Builder
		.create(Limlib.id("debug_maze"))
		.with("f", "copper_catacombs_f")
		.with("l", "copper_catacombs_l")
		.with("i", "copper_catacombs_i")
		.with("n", "copper_catacombs_n")
		.with("t", "copper_catacombs_t")
		.build();

	public static final MapCodec<DebugMazeChunkGenerator> CODEC = RecordCodecBuilder
		.mapCodec(instance -> instance
			.group(RegistryOps.retrieveElement(Biomes.THE_VOID))
			.apply(instance, instance.stable(DebugMazeChunkGenerator::new)));

	protected MazeGenerator<MazeComponent> generator = new MazeGenerator<>(8, 8, 16, 16, 0);

	public DebugMazeChunkGenerator(Holder<Biome> reference) {
		super(new FixedBiomeSource(reference), GROUP);
	}

	@Override
	public int getPlacementRadius() {
		return 1;
	}

	@Override
	public CompletableFuture<ChunkAccess> populateNoise(WorldGenRegion region, ServerLevel serverLevel, ChunkGenerator generator, ChunkAccess chunk, Blender blender, RandomState randomState, StructureManager structureManager) {
		this.generator.generateMaze(chunk, region, this::createMaze, this::decorateMaze);
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public int getGenDepth() {
		return 448;
	}

	@Override
	public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) {
		list.add("This is a debug world");
	}

	public MazeComponent createMaze(WorldGenRegion region, Vec2i mazePos, int width, int height, RandomSource random) {
		MazeComponent mazeToSolve = new DepthFirstMaze(width, height, random);
		mazeToSolve.generateMaze();

		MazeComponent mazeSolver = new DepthFirstMazeSolver(mazeToSolve, random,
			new Vec2i(random.nextIntBetweenInclusive(1,6), random.nextIntBetweenInclusive(1,6)),
			new Vec2i(0, 0), new Vec2i(7, 7), new Vec2i(7, 0), new Vec2i(0, 7));
		mazeSolver.generateMaze();
		return mazeSolver;
	}

	public void decorateMaze(WorldGenRegion region, Vec2i pos, Vec2i mazePos, MazeComponent maze, CellState state, Vec2i thickness,
	                         RandomSource random) {
		Pair<MazePiece, Manipulation> piece = MazePiece.getFromCell(state, random);

		if (piece.getFirst() != MazePiece.E) {
			int x = state.getPosition().getX();
			int y = state.getPosition().getY();

			if ((x == 0 && y == 0) || (x == 0 && y == 7) ||
				(x == 7 && y == 0) || (x == 7 && y == 7)) {
				generateNbt(region, pos.toBlock().atY(4), this.nbtGroup.pick("t", random));
			} else {
				generateNbt(region, pos.toBlock().atY(4), this.nbtGroup.pick(piece.getFirst().getAsLetter(), random), piece.getSecond());
			}
		}
	}
}
