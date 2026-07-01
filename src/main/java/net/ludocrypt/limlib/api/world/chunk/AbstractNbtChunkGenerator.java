package net.ludocrypt.limlib.api.world.chunk;

import java.util.Optional;

import net.ludocrypt.limlib.api.world.FunctionMap;
import net.ludocrypt.limlib.api.world.LimlibHelper;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.ludocrypt.limlib.api.world.NbtGroup;
import net.ludocrypt.limlib.api.world.NbtPlacerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.storage.loot.LootTable;

public abstract class AbstractNbtChunkGenerator extends LiminalChunkGenerator {

	protected NbtGroup nbtGroup;
	protected FunctionMap<ResourceLocation, NbtPlacerUtil, ResourceManager> structures;

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

	/**
	 * Use this to get the {@link NbtGroup} of the chunk generator in public context.
	 * It is preferred that you simply use the nbtGroup variable when extending {@link AbstractNbtChunkGenerator} instead.
	 */
	public NbtGroup getNbtGroup() {
		return this.nbtGroup;
	}

	public FunctionMap<ResourceLocation, NbtPlacerUtil, ResourceManager> getStructures() {
		return this.structures;
	}

	public void generateNbt(WorldGenRegion region, BlockPos at, ResourceLocation id) {
		generateNbt(region, at, id, Manipulation.NONE);
	}

	public void generateNbt(WorldGenRegion region, BlockPos at, ResourceLocation id, Manipulation manipulation) {
		try {
			getStructures()
				.eval(id, region.getServer().getResourceManager())
				.manipulate(manipulation)
				.generateNbt(region, at, (pos, state, nbt) -> this.modifyStructure(region, pos, state, nbt))
				.spawnEntities(region, at, manipulation);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Attempted to load undefined structure \'" + id + "\'");
		}

	}

	public void generateNbt(WorldGenRegion region, BlockPos offset, BlockPos from, BlockPos to, ResourceLocation id) {
		generateNbt(region, offset, from, to, id, Manipulation.NONE);
	}

	public void generateNbt(WorldGenRegion region, BlockPos offset, BlockPos from, BlockPos to, ResourceLocation id,
			Manipulation manipulation) {
		try {
			getStructures()
				.eval(id, region.getServer().getResourceManager())
				.manipulate(manipulation)
				.generateNbt(region, offset, from, to, (pos, state, nbt) -> this.modifyStructure(region, pos, state, nbt))
				.spawnEntities(region, offset, from, to, manipulation);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Attempted to load undefined structure \'" + id + "\'");
		}

	}

	protected void modifyStructure(WorldGenRegion region, BlockPos pos, BlockState state,
			Optional<CompoundTag> blockEntityNbt) {
		this.modifyStructure(region, pos, state, blockEntityNbt, Block.UPDATE_ALL);
	}

	protected void modifyStructure(WorldGenRegion region, BlockPos pos, BlockState state, Optional<CompoundTag> blockEntityNbt,
			int update) {

		if (!state.isAir()) {

			if (state.is(Blocks.BARRIER)) {
				region.setBlock(pos, Blocks.AIR.defaultBlockState(), update, 1);
			} else {
				region.setBlock(pos, state, update, 1);
			}

			if (blockEntityNbt.isPresent()) {
				BlockEntity blockEntity = region.getBlockEntity(pos);

				if (blockEntity != null) {

					if (state.is(blockEntity.getBlockState().getBlock())) {
						blockEntity.loadWithComponents(blockEntityNbt.get(), region.registryAccess());
					}

				}

				if (blockEntity instanceof RandomizableContainerBlockEntity lootTable) {
					lootTable
						.setLootTable(this.getContainerLootTable(lootTable), region.getSeed() + LimlibHelper.blockSeed(pos));
				}

			}

		}

	}

	protected ResourceKey<LootTable> getContainerLootTable(RandomizableContainerBlockEntity container) {
		return BuiltInLootTables.SIMPLE_DUNGEON;
	}

}
