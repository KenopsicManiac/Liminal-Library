package net.ludocrypt.limlib.api.effects.sky;

import java.util.Optional;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.DimensionSpecialEffects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * A Sky effects controller
 * <p>
 * This is a simplification of the base {@link DimensionEffects} class, where
 * each setting is a static, non-changing value
 */
public record StaticDimensionEffects(Optional<Float> cloudHeight, boolean alternateSkyColor, String skyType,
									 boolean brightenLighting, boolean darkened, boolean thickFog,
									 float skyShading) implements DimensionEffects {

	public static final StaticDimensionEffects EMPTY = new StaticDimensionEffects(Optional.empty(), false, "NONE", false, false, false, 1.0f);

	public static final MapCodec<StaticDimensionEffects> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		Codec.FLOAT.optionalFieldOf("cloud_height").stable().forGetter(StaticDimensionEffects::cloudHeight),
		Codec.BOOL.optionalFieldOf("alternate_sky_color", false).stable().forGetter(StaticDimensionEffects::alternateSkyColor),
		Codec.STRING.optionalFieldOf("sky_type", "NONE").stable().forGetter(StaticDimensionEffects::skyType),
		Codec.BOOL.optionalFieldOf("brighten_lighting", false).stable().forGetter(StaticDimensionEffects::brightenLighting),
		Codec.BOOL.optionalFieldOf("darkened", false).stable().forGetter(StaticDimensionEffects::darkened),
		Codec.BOOL.optionalFieldOf("thick_fog", false).stable().forGetter(StaticDimensionEffects::thickFog),
		Codec.FLOAT.optionalFieldOf("sky_shading", 1.0f).stable().forGetter(StaticDimensionEffects::skyShading)
	).apply(instance, instance.stable(StaticDimensionEffects::new)));

	@Override
	public MapCodec<? extends DimensionEffects> getCodec() {
		return CODEC;
	}

	public float getCloudHeight() {
		return cloudHeight.orElse(Float.NaN);
	}

	public boolean hasAlternateSkyColor() {
		return alternateSkyColor;
	}


	public boolean shouldBrightenLighting() {
		return brightenLighting;
	}


	public boolean hasThickFog() {
		return thickFog;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public DimensionSpecialEffects getDimensionEffects() {
		return SkyPropertiesCreator
			.create(getCloudHeight(), alternateSkyColor, skyType, brightenLighting, darkened,
				thickFog);
	}
}
