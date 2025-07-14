package net.ludocrypt.limlib.impl.mixin;

import java.util.List;
import java.util.concurrent.Executor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.maze.storage.MazeStorage;
import net.ludocrypt.limlib.api.world.maze.storage.MazeStorageProvider;
import net.ludocrypt.limlib.api.world.maze.storage.ServerWorldMazeAccess;
import net.ludocrypt.limlib.api.world.nbt.NbtTags;
import net.minecraft.server.MinecraftServer;

@Mixin(ServerLevel.class)
public class ServerWorldMixin implements ServerWorldMazeAccess {

	@Unique
	MazeStorage mazeStorage;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void limlib$init(MinecraftServer server, Executor executor, LevelStorageSource.LevelStorageAccess session,
							 ServerLevelData worldProperties, ResourceKey<Level> registryKey, LevelStem dimensionOptions,
							 ChunkProgressListener worldGenerationProgressListener, boolean bl, long l, List<CustomSpawner> spawners,
							 boolean shouldTickTime, @Nullable RandomSequences randomSequences, CallbackInfo ci) {

		if (dimensionOptions.generator() instanceof MazeStorageProvider provider) {
			this.mazeStorage = new MazeStorage(provider.generators(),
				session.getDimensionPath(registryKey).resolve("maze_region").toFile());
		}

		if (dimensionOptions.generator() instanceof AbstractNbtChunkGenerator generator) {

			if (generator.tags == null) {
				generator.tags = NbtTags.parse(generator.nbtGroup, server.getResourceManager());
			}

			generator.loadTags();
		}

	}

	@Inject(method = "saveLevelData", at = @At("TAIL"))
	private void limlib$saveWorld(CallbackInfo ci) {

		if (this.mazeStorage != null) {
			this.mazeStorage.save();
		}

	}

	@Override
	public MazeStorage getMazeStorage() {
		return this.mazeStorage;
	}

}
