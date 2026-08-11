package org.dimdev.limlib.client.specialmodels.mixin.sodium;

import net.caffeinemc.mods.sodium.client.gl.device.CommandList;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.DefaultChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.ChunkRenderListIterable;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.caffeinemc.mods.sodium.client.render.viewport.CameraTransform;
import org.dimdev.limlib.client.specialmodels.compat.sodium.SodiumCompat;
import org.dimdev.limlib.impl.Limlib;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DefaultChunkRenderer.class, remap = false)
public class DefaultChunkRendererMixin {

    private static boolean limlib$loggedTranslucentHook;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/ShaderChunkRenderer;end(Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/TerrainRenderPass;)V",
            shift = At.Shift.BEFORE))
    private void limlib$drawSpecialModelMeshesInTerrainPass(ChunkRenderMatrices matrices,
                                                            CommandList commandList,
                                                            ChunkRenderListIterable renderLists,
                                                            TerrainRenderPass terrainPass,
                                                            CameraTransform camera,
                                                            boolean useBlockFaceCulling,
                                                            CallbackInfo ci) {
        if (!terrainPass.isTranslucent()) {
            return;
        }

        if (!limlib$loggedTranslucentHook) {
            Limlib.LOGGER.info("[specialmodels/sodium] reached Sodium translucent terrain hook for special model meshes");
            limlib$loggedTranslucentHook = true;
        }

        SodiumCompat.renderSpecialModelMeshes(
            camera.x,
            camera.y,
            camera.z,
            new Matrix4f(matrices.modelView()),
            new Matrix4f(matrices.projection()));
    }
}
