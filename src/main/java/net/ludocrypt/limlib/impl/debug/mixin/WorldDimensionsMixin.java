package net.ludocrypt.limlib.impl.debug.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldDimensions;

@Mixin(WorldDimensions.class)
public abstract class WorldDimensionsMixin {

	@Shadow
	public abstract ChunkGenerator overworld();

	@Inject(method = "isDebug", at = @At("HEAD"), cancellable = true)
	public void limlib$isDebug(CallbackInfoReturnable<Boolean> ci) {

		if (this.overworld() instanceof DebugNbtChunkGenerator) {
			ci.setReturnValue(true);
		}

	}
}
