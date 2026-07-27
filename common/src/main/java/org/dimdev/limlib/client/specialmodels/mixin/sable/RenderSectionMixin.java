package org.dimdev.limlib.client.specialmodels.mixin.sable;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.dimdev.limlib.client.specialmodels.SpecialModelRenderTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SectionRenderDispatcher.RenderSection.class)
public abstract class RenderSectionMixin {
    @Shadow
    @Final
    private Map<RenderType, VertexBuffer> buffers;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void limlib$addSpecialModelBuffers(SectionRenderDispatcher dispatcher, int index, int originX, int originY, int originZ, CallbackInfo ci) {
        for (RenderType renderType : SpecialModelRenderTypes.chunkBufferLayers()) {
            this.buffers.computeIfAbsent(renderType, ignored -> new VertexBuffer(VertexBuffer.Usage.STATIC));
        }
    }
}
