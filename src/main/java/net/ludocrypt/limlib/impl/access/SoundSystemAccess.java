package net.ludocrypt.limlib.impl.access;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

public interface SoundSystemAccess {

	public void stopSoundsAtPosition(double x, double y, double z, @Nullable ResourceLocation id,
			@Nullable SoundSource category);

	public static SoundSystemAccess get(Object obj) {
		return (SoundSystemAccess) obj;
	}

}
