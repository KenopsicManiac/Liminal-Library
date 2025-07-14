package net.ludocrypt.limlib.api.world.chunk;

import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.nbt.FunctionMap;
import net.ludocrypt.limlib.api.world.nbt.NbtGroup;
import net.ludocrypt.limlib.api.world.nbt.NbtPlacerUtil;
import net.ludocrypt.limlib.api.world.nbt.NbtPlacerUtil.Bound;
import net.ludocrypt.limlib.api.world.nbt.NbtTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Optional;

public abstract class AbstractNbtChunkGenerator extends LiminalChunkGenerator {

	public final NbtGroup nbtGroup;
	public final FunctionMap<ResourceLocation, NbtPlacerUtil, ResourceManager> structures;
	public NbtTags tags;

	public AbstractNbtChunkGenerator(BiomeSource biomeSource, NbtGroup nbtGroup) {
		this(biomeSource, nbtGroup, new FunctionMap<ResourceLocation, NbtPlacerUtil, ResourceManager>(NbtPlacerUtil::load));
	}

	public AbstractNbtChunkGenerator(BiomeSource biomeSource, NbtGroup nbtGroup,
			FunctionMap<ResourceLocation, NbtPlacerUtil, ResourceManager> structures) {
		super(biomeSource);
		this.nbtGroup = nbtGroup;
		this.structures = structures;
		this.nbtGroup.fill(structures);
	}

	public abstract void loadTags();

	public void generateNbt(WorldGenRegion region, BlockPos at, ResourceLocation id) {
		generateNbt(region, at, id, Manipulation.NONE);
	}

	public void generateNbt(WorldGenRegion region, BlockPos at, ResourceLocation id, Manipulation manipulation) {

		try {
			structures
				.eval(id, region.getServer().getResourceManager())
				.manipulate(manipulation)
				.generateNbt(region, at, (pos, state, nbt) -> this.modifyStructure(region, pos, state, nbt))
				.spawnEntities(region, at, manipulation, (nbt) -> this.modifyEntity(region, nbt));
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Attempted to load undefined structure \'" + id + "\'");
		}

	}

	public void generateNbt(WorldGenRegion region, BlockPos at, ResourceLocation id, Manipulation manipulation,
			TriConsumer<BlockPos, BlockState, Optional<CompoundTag>> consumer) {

		try {
			structures
				.eval(id, region.getServer().getResourceManager())
				.manipulate(manipulation)
				.generateNbt(region, at, consumer)
				.spawnEntities(region, at, manipulation, (nbt) -> this.modifyEntity(region, nbt));
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Attempted to load undefined structure \'" + id + "\'");
		}

	}

	public void generateNbt(WorldGenRegion region, BlockPos offset, BlockPos from, Bound to, ResourceLocation id) {
		generateNbt(region, offset, from, to, id, Manipulation.NONE);
	}

	public void generateNbt(WorldGenRegion region, BlockPos offset, BlockPos from, Bound to, ResourceLocation id,
			Manipulation manipulation) {

		try {
			structures
				.eval(id, region.getServer().getResourceManager())
				.manipulate(manipulation)
				.generateNbt(region, offset, from, to, (pos, state, nbt) -> this.modifyStructure(region, pos, state, nbt))
				.spawnEntities(region, offset, from, to, manipulation, (nbt) -> this.modifyEntity(region, nbt));
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Attempted to load undefined structure \'" + id + "\'");
		}

	}

	public void generateNbt(WorldGenRegion region, BlockPos offset, BlockPos from, Bound to, ResourceLocation id,
			Manipulation manipulation, TriConsumer<BlockPos, BlockState, Optional<CompoundTag>> consumer) {

		try {
			structures
				.eval(id, region.getServer().getResourceManager())
				.manipulate(manipulation)
				.generateNbt(region, offset, from, to, consumer)
				.spawnEntities(region, offset, from, to, manipulation, (nbt) -> this.modifyEntity(region, nbt));
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Attempted to load undefined structure \'" + id + "\'");
		}

	}

	protected void modifyStructure(WorldGenRegion region, BlockPos pos, BlockState state,
			Optional<CompoundTag> blockEntityNbt) {
		this.modifyStructure(region, pos, state, blockEntityNbt, Block.UPDATE_ALL, 1);
	}

	protected void modifyStructure(WorldGenRegion region, BlockPos pos, BlockState state, Optional<CompoundTag> blockEntityNbt,
			int update, int depth) {

		if (state.is(Blocks.STRUCTURE_VOID)) {
			return;
		}

		region.setBlock(pos, state, update, depth);

		if (blockEntityNbt.isPresent()) {
			BlockEntity blockEntity = region.getBlockEntity(pos);

			if (blockEntity != null) {

				if (state.is(blockEntity.getBlockState().getBlock())) {
					blockEntity.load(blockEntityNbt.get());
				}

			}

			if (blockEntity instanceof RandomizableContainerBlockEntity lootTable) {
				lootTable
					.setLootTable(this.getContainerLootTable(lootTable), region.getSeed() + LimlibHelper.blockSeed(pos));
			}

		}

	}

	protected ResourceLocation getContainerLootTable(RandomizableContainerBlockEntity container) {
		return BuiltInLootTables.SIMPLE_DUNGEON;
	}

	protected CompoundTag modifyEntity(WorldGenRegion region, CompoundTag nbt) {
		return nbt;
	}

}
