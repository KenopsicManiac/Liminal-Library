package org.dimdev.limlib.client.specialmodels.compat.iris;

import org.dimdev.limlib.impl.Limlib;

public final class IrisCompat {

    public static boolean shouldDisableSpecialModelRenderTypes() {
        if (!Limlib.getSided().isModLoaded("iris")) {
            return false;
        }

        try {
            return IrisApiProxy.shouldDisableSpecialModelRenderTypes();
        } catch (LinkageError | RuntimeException exception) {
            return true;
        }
    }

    private IrisCompat() {
    }
}
