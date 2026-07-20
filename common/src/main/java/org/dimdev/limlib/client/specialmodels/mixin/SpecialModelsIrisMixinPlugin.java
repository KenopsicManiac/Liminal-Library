package org.dimdev.limlib.client.specialmodels.mixin;

import org.dimdev.limlib.util.AbstractClassDependentMixinPlugin;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.util.List;
import java.util.Set;

public final class SpecialModelsIrisMixinPlugin extends AbstractClassDependentMixinPlugin {

    public SpecialModelsIrisMixinPlugin() {
		super("net.irisshaders.iris.api.v0.IrisApi");
	}
}

