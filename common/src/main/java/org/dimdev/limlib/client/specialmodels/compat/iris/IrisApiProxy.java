package org.dimdev.limlib.client.specialmodels.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;

final class IrisApiProxy {

    static boolean isShaderPackInUse() {
        return IrisApi.getInstance().isShaderPackInUse();
    }

    static boolean areShadersEnabled() {
        return IrisApi.getInstance().getConfig().areShadersEnabled();
    }

    private IrisApiProxy() {
    }
}
