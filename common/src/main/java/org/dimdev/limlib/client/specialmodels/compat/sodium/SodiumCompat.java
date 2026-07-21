package org.dimdev.limlib.client.specialmodels.compat.sodium;

import org.dimdev.limlib.impl.Limlib;
import org.joml.Matrix4f;

public final class SodiumCompat {

    public static boolean isLoaded() {
        return Limlib.getSided().isModLoaded("sodium");
    }

    public static void renderSpecialModelMeshes(double cameraX, double cameraY, double cameraZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        if (isLoaded()) {
            SodiumSpecialModelMeshRegistry.renderAll(cameraX, cameraY, cameraZ, modelViewMatrix, projectionMatrix);
        }
    }

    private SodiumCompat() {
    }
}
