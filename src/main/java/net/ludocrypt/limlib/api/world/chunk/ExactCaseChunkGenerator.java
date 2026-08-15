package net.ludocrypt.limlib.api.world.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.pool.LimlibPoolApi;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A special form of dynamic chunk generator, intended for smaller dimensions with pre-determined piece placements.
 */
public class ExactCaseChunkGenerator extends AbstractDynamicChunkGenerator {
	public static final MapCodec<ExactCaseChunkGenerator> CODEC = RecordCodecBuilder
		.mapCodec(instance -> instance.group(
			BiomeSource.CODEC.fieldOf("biomeSource").forGetter(ExactCaseChunkGenerator::getBiomeSource),
			NbtGroup.CODEC.fieldOf("defaultGroup").forGetter(ExactCaseChunkGenerator::getStandardNbtGroup),
			ResourceLocation.CODEC.fieldOf("poolId").forGetter(ExactCaseChunkGenerator::getPoolId),
			ChunkPiece.CODEC.listOf().fieldOf("chunkPieces").forGetter(ExactCaseChunkGenerator::getPieces)
		).apply(instance, ExactCaseChunkGenerator::new));

	protected final ResourceLocation poolId;
	protected final List<ChunkPiece> pieces;

	public ExactCaseChunkGenerator(BiomeSource source, NbtGroup defaultGroup,
								   ResourceLocation poolId, List<ChunkPiece> pieces) {
		super(source, defaultGroup);
		this.poolId = poolId;
		this.pieces = pieces;
	}

	@Override
	public NbtGroup getDynamicGroup() {
		return LimlibPoolApi.getPoolAsGroup(poolId);
	}

	@Override
	public int getPlacementRadius() {
		return 0;
	}

	@Override
	public CompletableFuture<ChunkAccess> populateNoise(WorldGenRegion chunkRegion, ServerLevel serverLevel, ChunkGenerator generator, ChunkAccess chunk, Blender blender, RandomState randomState, StructureManager structureManager) {
		ChunkPos pos = chunk.getPos();
		for (ChunkPiece piece : pieces) {
			if (pos.x == piece.x && pos.z == piece.z) {
				RandomSource source = RandomSource.create(chunkRegion.getSeed() + pos.x + pos.z);
				BlockPos structurePos = pos.getWorldPosition();
				generateNbt(chunkRegion, structurePos, this.nbtGroup.pick(piece.subGroup, source));
				break;
			}
		}
		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public int getGenDepth() {
		return 0;
	}

	@Override
	public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) {

	}

	public record ChunkPiece(int x, int z, String subGroup) {
		public static Codec<ChunkPiece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("x").forGetter(ChunkPiece::x),
			Codec.INT.fieldOf("z").forGetter(ChunkPiece::z),
			Codec.STRING.fieldOf("sub_group").forGetter(ChunkPiece::subGroup)
		).apply(instance, ChunkPiece::new));
	}

	public ResourceLocation getPoolId() {
		return poolId;
	}

	public List<ChunkPiece> getPieces() {
		return pieces;
	}
}
