package org.dimdev.limlib.api.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
import net.minecraft.world.phys.Vec3;

public class SkyPropertiesCreator {

	public static DimensionSpecialEffects create(float cloudHeight, boolean alternateSkyColor, String skyType,
			boolean brightenLighting, boolean darkened, boolean thickFog) {
		return new DimensionSpecialEffects(cloudHeight, alternateSkyColor, SkyType.valueOf(skyType), brightenLighting,
			darkened) {

			@Override
			public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
				return color;
			}

			@Override
			public boolean isFoggyAt(int camX, int camY) {
				return thickFog;
			}

		};
	}

}
