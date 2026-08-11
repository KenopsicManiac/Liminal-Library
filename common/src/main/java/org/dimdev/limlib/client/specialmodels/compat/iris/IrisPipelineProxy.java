package org.dimdev.limlib.client.specialmodels.compat.iris;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import org.dimdev.limlib.impl.Limlib;
import org.jetbrains.annotations.Nullable;

final class IrisPipelineProxy {
    private static boolean loggedTerrainTranslucentFramebuffer;

    static void withTerrainTranslucentPhase(Runnable runnable) {
        IrisRenderingPipeline pipeline = getIrisPipeline();

        if (pipeline == null) {
            runnable.run();
            return;
        }

        pipeline.setOverridePhase(WorldRenderingPhase.TERRAIN_TRANSLUCENT);

        try {
            runnable.run();
        } finally {
            pipeline.setOverridePhase(null);
        }
    }

    static void bindTerrainTranslucentFramebuffer() {
        IrisRenderingPipeline pipeline = getIrisPipeline();

        if (pipeline != null) {
            GlFramebuffer framebuffer = pipeline.getSodiumPrograms().getFramebuffer(DefaultTerrainRenderPasses.TRANSLUCENT);
            framebuffer.bind();

            if (!loggedTerrainTranslucentFramebuffer) {
                loggedTerrainTranslucentFramebuffer = true;
                Limlib.LOGGER.info(
                    "[specialmodels/iris] bound Sodium translucent framebuffer for special model draw: fbo={}, attachments=[{}, {}, {}, {}], isBeforeTranslucent={}, flippedAfterTranslucent={}",
                    framebuffer.getId(),
                    framebuffer.getColorAttachment(0),
                    framebuffer.getColorAttachment(1),
                    framebuffer.getColorAttachment(2),
                    framebuffer.getColorAttachment(3),
                    pipeline.isBeforeTranslucent,
                    pipeline.getFlippedAfterTranslucent());
            }
        }
    }

    @Nullable
    private static IrisRenderingPipeline getIrisPipeline() {
        WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
        return pipeline instanceof IrisRenderingPipeline irisPipeline ? irisPipeline : null;
    }

    private IrisPipelineProxy() {
    }
}
