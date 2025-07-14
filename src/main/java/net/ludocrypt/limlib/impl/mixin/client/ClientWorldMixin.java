package net.ludocrypt.limlib.impl.mixin.client;

import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin extends Level {

	protected ClientWorldMixin(WritableLevelData worldProperties, ResourceKey<Level> registryKey,
							   RegistryAccess registryManager, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profiler,
							   boolean client, boolean debug, long seed, int maxChainedNeighborUpdates) {
		super(worldProperties, registryKey, registryManager, dimension, profiler, client, debug, seed,
			maxChainedNeighborUpdates);
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;forType(Lnet/minecraft/world/level/dimension/DimensionType;)Lnet/minecraft/client/renderer/DimensionSpecialEffects;"))
	private void limlib$init(ClientPacketListener netHandler, ClientLevel.ClientLevelData clientWorldProperties,
							 ResourceKey<Level> registryKey, Holder<DimensionType> dimensionType, int chunkManager, int simulationDistance,
							 Supplier<ProfilerFiller> profiler, LevelRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
		DimensionEffects.MIXIN_WORLD_LOOKUP
			.set(this.registryAccess().lookup(DimensionEffects.DIMENSION_EFFECTS_KEY).get());
	}

}
