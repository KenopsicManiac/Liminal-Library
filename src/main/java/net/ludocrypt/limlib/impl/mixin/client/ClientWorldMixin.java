package net.ludocrypt.limlib.impl.mixin.client;

import java.util.function.Supplier;

import net.ludocrypt.limlib.api.LimLibRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin extends Level {

	protected ClientWorldMixin(WritableLevelData worldProperties, ResourceKey<Level> registryKey,
			RegistryAccess registryManager, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profiler,
			boolean client, boolean debug, long seed, int maxChainedNeighborUpdates) {
		super(worldProperties, registryKey, registryManager, dimension, profiler, client, debug, seed,
			maxChainedNeighborUpdates);
	}

	@Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientPacketListener;Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/Holder;IILjava/util/function/Supplier;Lnet/minecraft/client/renderer/LevelRenderer;ZJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;forType(Lnet/minecraft/world/level/dimension/DimensionType;)Lnet/minecraft/client/renderer/DimensionSpecialEffects;", shift = Shift.BEFORE))
	private void limlib$init(ClientPacketListener netHandler, ClientLevel.ClientLevelData clientWorldProperties,
			ResourceKey<Level> registryKey, Holder<DimensionType> dimensionType, int chunkManager, int simulationDistance,
			Supplier<ProfilerFiller> profiler, LevelRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
		this.registryAccess().lookup(LimLibRegistries.DIMENSION_EFFECTS).ifPresent(DimensionEffects.MIXIN_WORLD_LOOKUP::set);
	}

}
