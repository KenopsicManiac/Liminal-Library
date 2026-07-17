package org.dimdev.limlib.impl.debug;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.limlib.api.world.NbtGroup;
import org.dimdev.limlib.api.world.chunk.AbstractDynamicChunkGenerator;
import org.dimdev.limlib.api.world.pool.LimlibPoolApi;
import org.dimdev.limlib.impl.Limlib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DebugDynamicChunkGenerator extends AbstractDynamicChunkGenerator {
	public static final NbtGroup DEFAULT = NbtGroup.Builder.create(Limlib.id("debug_dynamic"))
		.with("stone", "default_stone")
		.with("nether", "default_nether")
		.with("end", "default_end")
		.build();

	public static final MapCodec<DebugDynamicChunkGenerator> CODEC = RecordCodecBuilder
		.mapCodec(instance -> instance
			.group(RegistryOps.retrieveElement(Biomes.THE_VOID))
			.apply(instance, instance.stable(DebugDynamicChunkGenerator::new)));

	public DebugDynamicChunkGenerator(Holder.Reference<Biome> reference) {
		super(new FixedBiomeSource(reference), DEFAULT);
	}

	@Override
	public NbtGroup getDynamicGroup() {
		return LimlibPoolApi.getPoolAsGroup(Limlib.id("debug_dynamic"));
	}

	@Override
	public int getPlacementRadius() {
		return 0;
	}

	@Override
	public CompletableFuture<ChunkAccess> populateNoise(WorldGenRegion chunkRegion, ServerLevel level, ChunkGenerator generator, ChunkAccess chunk, Blender blender, RandomState randomState, StructureManager structureManager) {
		ChunkPos pos = chunk.getPos();
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				chunkRegion.setBlock(pos.getWorldPosition().offset(x, 3, y), Blocks.WHITE_CONCRETE.defaultBlockState(), Block.UPDATE_KNOWN_SHAPE);

				chunkRegion.setBlock(pos.getWorldPosition().offset(x, -16, y), Blocks.BARRIER.defaultBlockState(), Block.UPDATE_KNOWN_SHAPE);
			}
		}

		if (chunk.getPos().getWorldPosition().getX() == 0 && chunk.getPos().getWorldPosition().getZ() == 0) {
			return CompletableFuture.completedFuture(chunk);
		}

		RandomSource source = RandomSource.create(chunkRegion.getSeed() + pos.x + pos.z);
		BlockPos structurePos = pos.getWorldPosition().offset(6, 4, 6);
		int randInt = source.nextInt(3);

		if (randInt == 0) {
				generateNbt(chunkRegion, structurePos, this.nbtGroup.pick("stone", source));
		} else if (randInt == 1) {
				generateNbt(chunkRegion, structurePos, this.nbtGroup.pick("nether", source));
		} else if (randInt == 2){
				generateNbt(chunkRegion, structurePos, this.nbtGroup.pick("end", source));
		}

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

	}
}
