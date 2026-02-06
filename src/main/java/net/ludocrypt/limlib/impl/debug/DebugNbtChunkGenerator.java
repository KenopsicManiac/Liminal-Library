package net.ludocrypt.limlib.impl.debug;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.NbtPlacerUtil;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

public class DebugNbtChunkGenerator extends AbstractNbtChunkGenerator {

	public static final MapCodec<DebugNbtChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
		return instance
			.group(RegistryOps.retrieveElement(Biomes.THE_VOID))
			.apply(instance, instance.stable(DebugNbtChunkGenerator::new));
	});
	BidirectionalMap<ResourceLocation, BlockPos> positions = new BidirectionalMap<ResourceLocation, BlockPos>();

	public DebugNbtChunkGenerator(Holder.Reference<Biome> reference) {
		super(new FixedBiomeSource(reference), new DebugNbtGroup());
	}

	@Override
	protected MapCodec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public int getPlacementRadius() {
		return 4;
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
		return CompletableFuture.completedFuture(chunk);
	}


	@Override
	public CompletableFuture<ChunkAccess> populateNoise(WorldGenRegion chunkRegion,
														ServerLevel world, ChunkGenerator generator,
														ChunkAccess chunk) {

		if (chunk.getPos().getWorldPosition().getX() < 0 || chunk.getPos().getWorldPosition().getZ() < 0) {
			return CompletableFuture.completedFuture(chunk);
		}

		ResourceManager resourceManager = world.getServer().getResourceManager();

		if (positions.isEmpty()) {
			Map<ResourceLocation, List<Resource>> ids = resourceManager
				.listResourceStacks("structures/nbt", (id) -> id.getPath().endsWith(".nbt"));
			Map<ResourceLocation, NbtPlacerUtil> nbts = new LinkedHashMap<>();

			for (ResourceLocation id : ids.keySet()) {
				NbtPlacerUtil nbt = NbtPlacerUtil.load(id, resourceManager);
				nbts.put(id, nbt);
			}

			List<Map.Entry<ResourceLocation, NbtPlacerUtil>> sortedNbts = new ArrayList<>(nbts.entrySet());
			sortedNbts.sort(Map.Entry.comparingByKey());
			int maxSizeZ = 0;

			for (int i = 0; i < sortedNbts.size(); i++) {
				Map.Entry<ResourceLocation, NbtPlacerUtil> entry = sortedNbts.get(i);
				BlockPos prevPos;
				BlockPos prevSize;

				if (i == 0) {
					prevPos = BlockPos.ZERO;
					prevSize = BlockPos.ZERO.offset(-2, 0, 0);
				} else {
					prevPos = positions.get(sortedNbts.get(i - 1).getKey());
					prevSize = new BlockPos(sortedNbts.get(i - 1).getValue().sizeX, sortedNbts.get(i - 1).getValue().sizeY,
						sortedNbts.get(i - 1).getValue().sizeZ);
				}

				if (prevPos.getX() > 160) {
					prevPos = BlockPos.ZERO.offset(-prevSize.getX() - 2, 0, prevPos.getZ() + maxSizeZ + 2);
					maxSizeZ = 0;
				}

				if (entry.getValue().sizeZ > maxSizeZ) {
					maxSizeZ = entry.getValue().sizeZ;
				}

				positions.put(entry.getKey(), prevPos.offset(prevSize.getX() + 2, 0, 0));
				this.nbtGroup
					.getGroups()
					.computeIfAbsent("debug", (s) -> Lists.newArrayList())
					.add(entry.getKey().toString());
			}

			this.nbtGroup.fill(this.structures);
		}

		for (int x = 0; x < 16; x++) {

			for (int z = 0; z < 16; z++) {
				BlockPos pos = chunk.getPos().getWorldPosition().offset(x, 10, z);

				if (positions.reverseMap.containsKey(pos.offset(0, -10, 0))) {
					ResourceLocation id = positions.reverseMap.get(pos.offset(0, -10, 0));
					this.generateNbt(chunkRegion, pos, id);
					chunkRegion
						.setBlock(pos.offset(-1, -1, -1),
							Blocks.STRUCTURE_BLOCK.defaultBlockState().setValue(StructureBlock.MODE, StructureMode.SAVE),
							Block.UPDATE_KNOWN_SHAPE);
					BlockEntity be = chunkRegion.getBlockEntity(pos.offset(-1, -1, -1));

					if (be != null && be instanceof StructureBlockEntity blockEntity) {
						blockEntity
							.setStructureSize(new Vec3i(this.structures.eval(id, resourceManager).sizeX,
								this.structures.eval(id, resourceManager).sizeY,
								this.structures.eval(id, resourceManager).sizeZ));
						blockEntity
							.setStructureName(
								id.toString().substring(0, id.toString().length() - 4).replaceFirst("structures/", ""));
						blockEntity.setStructurePos(new BlockPos(1, 1, 1));
						blockEntity.setIgnoreEntities(false);
					}

				}

				chunkRegion.setBlock(pos.offset(0, -10, 0), Blocks.BARRIER.defaultBlockState(), Block.UPDATE_KNOWN_SHAPE);
			}

		}

		return CompletableFuture.completedFuture(chunk);
	}

	@Override
	public int getGenDepth() {
		return 448;
	}

	@Override
	protected void modifyStructure(WorldGenRegion region, BlockPos pos, BlockState state, Optional<CompoundTag> blockEntityNbt,
			int update) {
		region.setBlock(pos, state, update, 1);
		blockEntityNbt.ifPresent((nbt) -> {
			if (region.getBlockEntity(pos) != null)
				region.getBlockEntity(pos).loadWithComponents(nbt, region.registryAccess());
		});
	}

	@Override
	public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {
	}

	public static class DebugNbtGroup extends NbtGroup {

		public DebugNbtGroup() {
			super(ResourceLocation.parse("debug"), Maps.newHashMap());
		}

		@Override
		public ResourceLocation nbtId(String group, String nbt) {
			return ResourceLocation.parse(nbt);
		}

	}

	public static class BidirectionalMap<K, V> {

		private Map<K, V> forwardMap = new HashMap<>();
		private Map<V, K> reverseMap = new HashMap<>();

		public void put(K key, V value) {
			forwardMap.put(key, value);
			reverseMap.put(value, key);
		}

		public V get(K key) {
			return forwardMap.get(key);
		}

		public K invertGet(V value) {
			return reverseMap.get(value);
		}

		public boolean isEmpty() {
			return forwardMap.isEmpty() || reverseMap.isEmpty();
		}

	}

}
