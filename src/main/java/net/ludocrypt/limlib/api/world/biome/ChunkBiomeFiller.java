package net.ludocrypt.limlib.api.world.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Arrays;
import java.util.List;

public class ChunkBiomeFiller {
	private final List<Holder<Biome>> biomes;

	public ChunkBiomeFiller(List<Holder<Biome>> biomes) {
		this.biomes = biomes;
	}

	public ChunkAccess fillChunk(ChunkAccess chunk, ServerLevel level, BiomeRegion... regions) {
		return fillChunk(chunk, level, 0, regions);
	}

	public ChunkAccess fillChunk(ChunkAccess chunk, ServerLevel level, int chunkStartY, BiomeRegion... regions) {
		ChunkPos chunkPos = chunk.getPos();
		BlockPos chunkStart = new BlockPos(chunkPos.getMinBlockX(), chunkStartY, chunkPos.getMinBlockZ());
		Arrays.stream(regions).forEach(region -> {
			// I'm not remaking the wheel bro
			FillBiomeCommand.fill(level,
				chunkStart.offset(region.startPos), chunkStart.offset(region.endPos), biomes.get(region.biomeFlag));
		});
		return chunk;
	}

	public record BiomeRegion(BlockPos startPos, BlockPos endPos, int biomeFlag) {}
}
