package net.ludocrypt.limlib.datagen.providers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.ludocrypt.limlib.api.world.pool.PiecePool;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class PiecePoolDataProvider extends FabricCodecDataProvider<PiecePool> {
	protected PiecePoolDataProvider(FabricDataOutput dataOutput, CompletableFuture<Provider> registriesFuture) {
		super(dataOutput, registriesFuture, Target.DATA_PACK, "worldgen/piece_pools", PiecePool.CODEC);
	}

	protected void providePool(BiConsumer<ResourceLocation, PiecePool> provider, ResourceLocation id, Map<String, List<String>> subPoolMap) {
		provider.accept(id, new PiecePool(id, subPoolMap));
	}

	@Override
	public String getName() {
		return "PiecePool Data Provider";
	}
}
