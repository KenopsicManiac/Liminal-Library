package net.ludocrypt.limlib.api.effects.sky;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * A Sky effects controller
 * <p>
 * This is a completely empty, default setting version of
 * {@link StaticDimensionEffects}
 */
public class EmptyDimensionEffects extends StaticDimensionEffects {

	public static final MapCodec<EmptyDimensionEffects> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.stable(new EmptyDimensionEffects()));

	public EmptyDimensionEffects() {
		super(Optional.empty(), false, "NONE", false, false, false, 1.0F);
	}

	@Override
	public MapCodec<? extends DimensionEffects> getCodec() {
		return CODEC;
	}

}
