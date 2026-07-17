package org.dimdev.limlib.api.effects.sky;

import java.util.concurrent.atomic.AtomicReference;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import org.dimdev.limlib.api.LimLibRegistires;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.impl.Limlib;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.HolderLookup;

/**
 * A non-client-side clone of {@link DimensionSpecialEffects}
 */
public interface DimensionEffects {
	Codec<DimensionEffects> CODEC = DimensionEffectsType.CODEC.dispatch(DimensionEffects::type, DimensionEffectsType::codec);
	AtomicReference<HolderLookup.RegistryLookup<DimensionEffects>> MIXIN_WORLD_LOOKUP = new AtomicReference<>();

	DimensionEffectsType<? extends DimensionEffects> type();

	float skyShading();

	record DimensionEffectsType<T extends DimensionEffects>(MapCodec<T> codec) {
		public static final Codec<DimensionEffects.DimensionEffectsType<? extends DimensionEffects>> CODEC = LimLibRegistires.DIMENSION_EFFECTS_TYPE.byNameCodec();

		public static final DimensionEffects.DimensionEffectsType<StaticDimensionEffects> STATIC = register("static", StaticDimensionEffects.CODEC);

		public static void register() {}

		static <U extends DimensionEffects> DimensionEffects.DimensionEffectsType<U> register(String id, MapCodec<U> codec) {
			return Limlib.getSided().register(LimLibRegistryKeys.DIMENSION_EFFECTS_TYPE, id, new DimensionEffects.DimensionEffectsType<>(codec));
		}
	}
}
