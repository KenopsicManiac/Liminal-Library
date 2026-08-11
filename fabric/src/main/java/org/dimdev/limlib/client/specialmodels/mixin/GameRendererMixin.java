package org.dimdev.limlib.client.specialmodels.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.dimdev.limlib.client.specialmodels.FabricSpecialModelShaderRegistrar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(
        method = "reloadShaders",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;loadBlurEffect(Lnet/minecraft/server/packs/resources/ResourceProvider;)V"))
    private void limlib$registerSpecialModelShaders(
        ResourceProvider provider,
        CallbackInfo ci,
        @Local(index = 3) List<Pair<ShaderInstance, Consumer<ShaderInstance>>> programs) throws IOException {
        FabricSpecialModelShaderRegistrar.register(provider, programs);
    }
}
