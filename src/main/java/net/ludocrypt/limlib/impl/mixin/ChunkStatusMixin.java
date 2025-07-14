package net.ludocrypt.limlib.impl.mixin;

import com.mojang.datafixers.util.Either;
import net.ludocrypt.limlib.api.world.chunk.LiminalChunkGenerator;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(ChunkStatus.class)
public class ChunkStatusMixin {

	@Inject(method = "method_38284", at = @At("HEAD"), cancellable = true)
	private static void limlib$liminalChunkGenerator(ChunkStatus chunkStatus, Executor executor, ServerLevel serverWorld,
													 ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager,
													 ThreadedLevelLightEngine serverLightingProvider,
													 Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> function, List<ChunkAccess> chunks,
													 ChunkAccess chunk, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> ci) {

		if (chunkGenerator instanceof LiminalChunkGenerator limChunkGen) {
			WorldGenRegion chunkRegion = new WorldGenRegion(serverWorld, chunks, chunkStatus, limChunkGen.getPlacementRadius());
			ci
				.setReturnValue(limChunkGen
					.populateNoise(chunkRegion, chunkStatus, executor, serverWorld, chunkGenerator, structureTemplateManager,
						serverLightingProvider, function, chunks, chunk)
					.thenApply(chunkx -> {

						if (chunkx instanceof net.minecraft.world.level.chunk.ProtoChunk protoChunk) {
							net.minecraft.world.level.levelgen.BelowZeroRetrogen belowZeroRetrogen = protoChunk.getBelowZeroRetrogen();

							if (belowZeroRetrogen != null) {
								BelowZeroRetrogen.replaceOldBedrock(protoChunk);

								if (belowZeroRetrogen.hasBedrockHoles()) {
									belowZeroRetrogen.applyBedrockMask(protoChunk);
								}

							}

						}

						return Either.left(chunkx);
					}));
		}

	}

}
