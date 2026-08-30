package net.ludocrypt.limlib.impl.services;

import org.jetbrains.annotations.Contract;

/**
 * Service to check if Iris is running shaders or not.
 * This was used for the now-deprecated Skybox API, please
 * use Nuit to handle skyboxes instead.
 * <br> <br>
 * To use this, first check if Iris is being used via {@link net.fabricmc.loader.api.FabricLoader#isModLoaded(String)}.
 * <br> Next, run {@code ServiceLoader.load(IrisCompatImpl.class)} to get a ServiceLoader, and iterate over it in a for-loop
 * @see java.util.ServiceLoader
 */
public interface IrisCompatImpl {
	/**
	 * @return Whether a shader-pack is being used or not
	 */
	@Contract(pure = true)
	boolean shadersInUse();

	/**
	 * @return If Iris is rendering the Shadow Pass or not
	 */
	@Contract(pure = true)
	boolean isRenderingShadowPass();
}
