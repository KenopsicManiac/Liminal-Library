package net.ludocrypt.limlib.api.effects.sky;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.ludocrypt.limlib.api.LimLibRegistries;
import net.ludocrypt.limlib.api.Utils;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;

/**
 * A non-client-side clone of {@link DimensionSpecialEffects}
 */
public interface DimensionEffects {
	MappedRegistry<MapCodec<? extends DimensionEffects>> REGISTRY = Utils.createRegistry(LimLibRegistries.DIMENSION_EFFECTS_CODEC);
	Codec<DimensionEffects> CODEC = REGISTRY
		.byNameCodec()
		.dispatchStable(DimensionEffects::getCodec, Function.identity());
	AtomicReference<HolderLookup.RegistryLookup<DimensionEffects>> MIXIN_WORLD_LOOKUP = new AtomicReference<>();

	public static void init() {
		Utils.register(REGISTRY, "static", StaticDimensionEffects.CODEC);
		DynamicRegistries.registerSynced(LimLibRegistries.DIMENSION_EFFECTS, DimensionEffects.CODEC);
	}

	MapCodec<? extends DimensionEffects> getCodec();

	@Environment(EnvType.CLIENT)
	DimensionSpecialEffects getDimensionEffects();

	float skyShading();
}
