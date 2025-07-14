package net.ludocrypt.limlib.api.effects.sky;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.ludocrypt.limlib.impl.mixin.RegistriesAccessor;

/**
 * A non-client-side clone of {@link DimensionSpecialEffects}
 */
public abstract class DimensionEffects {

	public static final ResourceKey<Registry<Codec<? extends DimensionEffects>>> DIMENSION_EFFECTS_CODEC_KEY = ResourceKey
		.createRegistryKey(new ResourceLocation("limlib/codec/dimension_effects"));
	public static final Registry<Codec<? extends DimensionEffects>> DIMENSION_EFFECTS_CODEC = RegistriesAccessor
		.callRegisterSimple(DIMENSION_EFFECTS_CODEC_KEY, Lifecycle.stable(), (registry) -> StaticDimensionEffects.CODEC);
	public static final Codec<DimensionEffects> CODEC = DIMENSION_EFFECTS_CODEC
		.byNameCodec()
		.dispatchStable(DimensionEffects::getCodec, Function.identity());
	public static final ResourceKey<Registry<DimensionEffects>> DIMENSION_EFFECTS_KEY = ResourceKey
		.createRegistryKey(new ResourceLocation("limlib/dimension_effects"));

//	@ModInternal
	public static final AtomicReference<HolderLookup<DimensionEffects>> MIXIN_WORLD_LOOKUP = new AtomicReference<HolderLookup<DimensionEffects>>();

	public abstract Codec<? extends DimensionEffects> getCodec();

	public static void init() {
		Registry
			.register(DimensionEffects.DIMENSION_EFFECTS_CODEC, new ResourceLocation("limlib", "static"),
				StaticDimensionEffects.CODEC);
		Registry
			.register(DimensionEffects.DIMENSION_EFFECTS_CODEC, new ResourceLocation("limlib", "empty"),
				EmptyDimensionEffects.CODEC);
	}

	@Environment(EnvType.CLIENT)
	public abstract DimensionSpecialEffects getDimensionEffects();

	public abstract float getSkyShading();

}
