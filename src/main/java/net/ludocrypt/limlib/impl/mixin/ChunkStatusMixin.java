package net.ludocrypt.limlib.impl.mixin;

import java.util.concurrent.CompletableFuture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ludocrypt.limlib.api.world.chunk.AbstractNbtChunkGenerator;
import net.ludocrypt.limlib.api.world.chunk.DynamicNbtUpdater;
import net.minecraft.server.level.*;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


import net.ludocrypt.limlib.api.world.chunk.LiminalChunkGenerator;
import net.minecraft.world.level.chunk.ChunkAccess;

@Mixin(ChunkStatusTasks.class)
public class ChunkStatusMixin {
	@WrapOperation(method = "generateNoise", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/ChunkGenerator;fillFromNoise(Lnet/minecraft/world/level/levelgen/blending/Blender;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkAccess;)Ljava/util/concurrent/CompletableFuture;"))
	private static CompletableFuture<ChunkAccess> limlib$liminalChunkGenerator1(ChunkGenerator instance, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess, Operation<CompletableFuture<ChunkAccess>> original, @Local WorldGenRegion worldGenRegion, @Local ServerLevel serverLevel) {
		if (instance instanceof LiminalChunkGenerator liminalChunkGenerator) {
			if (liminalChunkGenerator instanceof AbstractNbtChunkGenerator nbtChunkGenerator) {
				if (nbtChunkGenerator instanceof DynamicNbtUpdater updater) {
					updater.update(nbtChunkGenerator);
				}
			}

			return liminalChunkGenerator.populateNoise(worldGenRegion, serverLevel, instance, chunkAccess, blender, randomState, structureManager);
		}

		return original.call(instance, blender, randomState, structureManager, chunkAccess);
	}
}
