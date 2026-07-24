package org.dimdev.limlib.client.specialmodels.mixin;

import org.spongepowered.asm.service.MixinService;

public final class SpecialModelsSableSodiumMixinPlugin extends SpecialModelsSableMixinPlugin {
    private final boolean sodiumLoaded;

    public SpecialModelsSableSodiumMixinPlugin() {
        this.sodiumLoaded = isClassLoaded("net.caffeinemc.mods.sodium.client.SodiumClientMod");
    }

    private static boolean isClassLoaded(String targetClass) {
        try {
            return MixinService.getService()
                .getBytecodeProvider()
                .getClassNode(targetClass) != null;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return super.shouldApplyMixin(targetClassName, mixinClassName) && this.sodiumLoaded;
    }
}
