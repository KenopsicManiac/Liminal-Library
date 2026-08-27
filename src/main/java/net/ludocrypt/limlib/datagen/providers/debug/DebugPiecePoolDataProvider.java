package net.ludocrypt.limlib.datagen.providers.debug;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.ludocrypt.limlib.api.world.pool.PiecePool;
import net.ludocrypt.limlib.datagen.providers.PiecePoolDataProvider;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class DebugPiecePoolDataProvider extends PiecePoolDataProvider {
	public DebugPiecePoolDataProvider(FabricDataOutput dataOutput, CompletableFuture<Provider> registriesFuture) {
		super(dataOutput, registriesFuture);
	}

	@Override
	public void configure(BiConsumer<ResourceLocation, PiecePool> provider, Provider lookup) {
		provider.accept(Limlib.id("debug_dynamic"), new PiecePool(Limlib.id("debug_dynamic"),
			Map.of(
				"stone", List.of("default_stone", "data_stone"),
				"nether", List.of("default_nether", "data_nether"),
				"end", List.of("default_end", "data_end")
			)));
	}
}
