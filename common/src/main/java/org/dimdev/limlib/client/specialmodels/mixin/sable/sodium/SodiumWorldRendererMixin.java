package org.dimdev.limlib.client.specialmodels.mixin.sable.sodium;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.dimdev.limlib.client.specialmodels.SpecialModelRenderTypes;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public abstract class SodiumWorldRendererMixin {

    @Shadow
    private ClientLevel level;

    @Inject(method = "drawChunkLayer", at = @At("TAIL"))
    private void limlib$drawSableSpecialModelLayers(RenderType renderType, ChunkRenderMatrices matrices,
                                                    double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (renderType != RenderType.translucent()) {
            return;
        }

        SubLevelRenderDispatcher renderDispatcher = SubLevelRenderDispatcher.get();
        Minecraft minecraft = Minecraft.getInstance();
        List<ClientSubLevel> sublevels = SubLevelContainer.getContainer(this.level).getAllSubLevels();
        Matrix4f modelView = new Matrix4f(matrices.modelView());
        Matrix4f projection = new Matrix4f(matrices.projection());
        float partialTicks = minecraft.getTimer().getGameTimeDeltaPartialTick(false);

        for (RenderType specialModelLayer : SpecialModelRenderTypes.chunkBufferLayers()) {
            specialModelLayer.setupRenderState();
            ShaderInstance shader = Objects.requireNonNull(RenderSystem.getShader(), "shader");
            shader.setDefaultUniforms(specialModelLayer.mode(), modelView, projection, minecraft.getWindow());
            shader.apply();

            renderDispatcher.renderSectionLayer(sublevels, specialModelLayer, shader, cameraX, cameraY, cameraZ, modelView, projection, partialTicks);

            shader.clear();
            specialModelLayer.clearRenderState();
        }
    }
}
