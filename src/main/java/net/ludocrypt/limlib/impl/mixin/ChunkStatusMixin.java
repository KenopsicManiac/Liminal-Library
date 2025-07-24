package net.ludocrypt.limlib.impl.mixin;

import java.util.concurrent.CompletableFuture;

import net.minecraft.server.level.*;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import net.ludocrypt.limlib.api.world.chunk.LiminalChunkGenerator;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;

@Mixin(ChunkStatusTasks.class)
public class ChunkStatusMixin {

	@Inject(method = "generateNoise", at = @At("HEAD"), cancellable = true)
	private static void limlib$liminalChunkGenerator(WorldGenContext worldGenContext, ChunkStep chunkStep, StaticCache2D<GenerationChunkHolder> chunks, ChunkAccess chunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
		var chunkGenerator = worldGenContext.generator();

		if (chunkGenerator instanceof LiminalChunkGenerator limChunkGen) {
			WorldGenRegion chunkRegion = new WorldGenRegion(worldGenContext.level(), chunks, chunkStep, chunk);
			cir
				.setReturnValue(limChunkGen
					.populateNoise(chunkRegion, worldGenContext.level(), chunkGenerator,
						chunk)
					.thenApply(chunkx -> {

						if (chunkx instanceof ProtoChunk protoChunk) {
							BelowZeroRetrogen belowZeroRetrogen = protoChunk.getBelowZeroRetrogen();

							if (belowZeroRetrogen != null) {
								BelowZeroRetrogen.replaceOldBedrock(protoChunk);

								if (belowZeroRetrogen.hasBedrockHoles()) {
									belowZeroRetrogen.applyBedrockMask(protoChunk);
								}

							}

						}

						return chunkx;
					}));
		}

	}

}
