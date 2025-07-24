package net.ludocrypt.limlib.api.effects.sky;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * A non-client-side clone of {@link DimensionSpecialEffects}
 */
public abstract class DimensionEffects {

	public static final ResourceKey<Registry<MapCodec<? extends DimensionEffects>>> DIMENSION_EFFECTS_CODEC_KEY = ResourceKey
		.createRegistryKey(ResourceLocation.parse("limlib/codec/dimension_effects"));
	public static final Registry<MapCodec<? extends DimensionEffects>> DIMENSION_EFFECTS_CODEC = RegistriesAccessor
		.callRegisterSimple(DIMENSION_EFFECTS_CODEC_KEY, (registry) -> StaticDimensionEffects.CODEC);
	public static final Codec<DimensionEffects> CODEC = DIMENSION_EFFECTS_CODEC
		.byNameCodec()
		.dispatchStable(DimensionEffects::getCodec, Function.identity());
	public static final ResourceKey<Registry<DimensionEffects>> DIMENSION_EFFECTS_KEY = ResourceKey
		.createRegistryKey(ResourceLocation.parse("limlib/dimension_effects"));

	public static final AtomicReference<HolderLookup<DimensionEffects>> MIXIN_WORLD_LOOKUP = new AtomicReference<HolderLookup<DimensionEffects>>();

	public abstract MapCodec<? extends DimensionEffects> getCodec();

	@Environment(EnvType.CLIENT)
	public abstract DimensionSpecialEffects getDimensionEffects();

	public abstract float getSkyShading();

}
