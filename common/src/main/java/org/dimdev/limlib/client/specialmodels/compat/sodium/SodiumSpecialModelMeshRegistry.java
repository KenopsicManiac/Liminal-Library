package org.dimdev.limlib.client.specialmodels.compat.sodium;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.dimdev.limlib.client.specialmodels.SpecialModelRenderTypes;
import org.dimdev.limlib.client.specialmodels.compat.iris.IrisCompat;
import org.dimdev.limlib.impl.Limlib;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class SodiumSpecialModelMeshRegistry {

    private static final ConcurrentLinkedQueue<PendingSectionMeshes> pendingSections = new ConcurrentLinkedQueue<>();
    private static final Map<Long, UploadedSectionMeshes> uploadedSections = new LinkedHashMap<>();
    private static boolean loggedSubmit;
    private static boolean loggedNoUploadedSections;
    private static boolean loggedDrawSummary;
    private static boolean loggedFirstDrawGlState;
    private static boolean loggedFirstDrawSamples;

    public static PendingMeshBuilder createBuilder(RenderType renderType) {
        ByteBufferBuilder byteBuffer = new ByteBufferBuilder(renderType.bufferSize());
        return new PendingMeshBuilder(byteBuffer, new BufferBuilder(byteBuffer, renderType.mode(), renderType.format()));
    }

    public static void submit(SectionPos sectionPos, Map<RenderType, PendingMeshBuilder> builders) {
        Map<RenderType, PendingMesh> meshes = new LinkedHashMap<>();

        for (Map.Entry<RenderType, PendingMeshBuilder> entry : builders.entrySet()) {
            PendingMesh mesh = entry.getValue().build();

            if (mesh != null) {
                meshes.put(entry.getKey(), mesh);
            }
        }

        pendingSections.add(new PendingSectionMeshes(sectionPos.asLong(), meshes));

        if (!loggedSubmit && !meshes.isEmpty()) {
            Limlib.LOGGER.info("[specialmodels/sodium] submitted special model meshes for section {}; layers={}",
                sectionPos, meshes.size());
            loggedSubmit = true;
        }
    }

    public static void remove(SectionPos sectionPos) {
        pendingSections.add(new PendingSectionMeshes(sectionPos.asLong(), Map.of()));
    }

    public static void renderAll(double cameraX, double cameraY, double cameraZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        uploadPending();

        if (uploadedSections.isEmpty()) {
            if (!loggedNoUploadedSections && !SpecialModelRenderTypes.chunkBufferLayers().isEmpty()) {
                Limlib.LOGGER.info("[specialmodels/sodium] Sodium special model render path reached with no uploaded sections");
                loggedNoUploadedSections = true;
            }

            return;
        }

        int drawCalls = 0;

        for (RenderType renderType : SpecialModelRenderTypes.chunkBufferLayers()) {
            boolean drawing = false;

            for (Map.Entry<Long, UploadedSectionMeshes> uploadedEntry : uploadedSections.entrySet()) {
                UploadedSectionMeshes sectionMeshes = uploadedEntry.getValue();
                VertexBuffer vertexBuffer = sectionMeshes.meshes().get(renderType);

                if (vertexBuffer == null) {
                    continue;
                }

                if (!drawing) {
                    renderType.setupRenderState();
                    IrisCompat.bindSpecialModelTerrainFramebuffer();
                    drawing = true;
                }

                ShaderInstance shader = RenderSystem.getShader();

                if (shader != null) {
                    shader.setDefaultUniforms(renderType.mode(), modelViewMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
                    shader.apply();
                    IrisCompat.bindSpecialModelTerrainFramebuffer();

                    try (SpecialModelDrawState ignored = SpecialModelDrawState.apply()) {
                        logFirstDrawGlState(renderType, shader);

                        if (shader.CHUNK_OFFSET != null) {
                            BlockPos sectionOrigin = SectionPos.of(uploadedEntry.getKey()).origin();
                            shader.CHUNK_OFFSET.set(
                                (float) ((double) sectionOrigin.getX() - cameraX),
                                (float) ((double) sectionOrigin.getY() - cameraY),
                                (float) ((double) sectionOrigin.getZ() - cameraZ));
                            shader.CHUNK_OFFSET.upload();
                        }

                        int sampleQuery = beginFirstDrawSampleQuery();

                        vertexBuffer.bind();
                        vertexBuffer.draw();
                        endFirstDrawSampleQuery(sampleQuery, renderType, shader);
                        drawCalls++;

                        if (shader.CHUNK_OFFSET != null) {
                            shader.CHUNK_OFFSET.set(0.0F, 0.0F, 0.0F);
                            shader.CHUNK_OFFSET.upload();
                        }
                    }

                    shader.clear();
                }
            }

            if (drawing) {
                VertexBuffer.unbind();
                renderType.clearRenderState();
            }
        }

        if (!loggedDrawSummary) {
            Limlib.LOGGER.info(
                "[specialmodels/sodium] rendered special model meshes through Sodium path: uploadedSections={}, drawCalls={}",
                uploadedSections.size(), drawCalls);
            loggedDrawSummary = true;
        }
    }

    private static void logFirstDrawGlState(RenderType renderType, ShaderInstance shader) {
        if (loggedFirstDrawGlState) {
            return;
        }

        loggedFirstDrawGlState = true;

        int drawFramebuffer = GL11C.glGetInteger(GL30C.GL_DRAW_FRAMEBUFFER_BINDING);
        int readFramebuffer = GL11C.glGetInteger(GL30C.GL_READ_FRAMEBUFFER_BINDING);
        int framebufferStatus = GL30C.glCheckFramebufferStatus(GL30C.GL_DRAW_FRAMEBUFFER);
        int currentProgram = GL11C.glGetInteger(GL20C.GL_CURRENT_PROGRAM);
        int drawBuffer0 = GL11C.glGetInteger(GL20C.GL_DRAW_BUFFER0);
        int drawBuffer1 = GL11C.glGetInteger(GL20C.GL_DRAW_BUFFER1);
        int drawBuffer2 = GL11C.glGetInteger(GL20C.GL_DRAW_BUFFER2);
        int drawBuffer3 = GL11C.glGetInteger(GL20C.GL_DRAW_BUFFER3);

        Limlib.LOGGER.info(
            "[specialmodels/sodium] first special model draw GL state: renderType={}, shader={}, shaderClass={}, shaderProgram={}, currentProgram={}, irisPack={}, drawFbo={}, readFbo={}, framebufferStatus={}, drawBuffers=[{}, {}, {}, {}], indexedBlend=[{}, {}, {}, {}], colorMasks=[{}, {}, {}, {}], depthTest={}, depthMask={}, blend={}, cull={}",
            renderType,
            shader.getName(),
            shader.getClass().getName(),
            shader.getId(),
            currentProgram,
            IrisCompat.currentShaderPackName(),
            drawFramebuffer,
            readFramebuffer,
            framebufferStatus,
            drawBuffer0,
            drawBuffer1,
            drawBuffer2,
            drawBuffer3,
            GL30C.glIsEnabledi(GL11C.GL_BLEND, 0),
            GL30C.glIsEnabledi(GL11C.GL_BLEND, 1),
            GL30C.glIsEnabledi(GL11C.GL_BLEND, 2),
            GL30C.glIsEnabledi(GL11C.GL_BLEND, 3),
            ColorMask.capture(0),
            ColorMask.capture(1),
            ColorMask.capture(2),
            ColorMask.capture(3),
            GL11C.glIsEnabled(GL11C.GL_DEPTH_TEST),
            GL11C.glGetBoolean(GL11C.GL_DEPTH_WRITEMASK),
            GL11C.glIsEnabled(GL11C.GL_BLEND),
            GL11C.glIsEnabled(GL11C.GL_CULL_FACE));
    }

    private static int beginFirstDrawSampleQuery() {
        if (loggedFirstDrawSamples) {
            return 0;
        }

        int query = GL15C.glGenQueries();
        GL15C.glBeginQuery(GL15C.GL_SAMPLES_PASSED, query);
        return query;
    }

    private static void endFirstDrawSampleQuery(int query, RenderType renderType, ShaderInstance shader) {
        if (query == 0) {
            return;
        }

        GL15C.glEndQuery(GL15C.GL_SAMPLES_PASSED);
        int samplesPassed = GL15C.glGetQueryObjecti(query, GL15C.GL_QUERY_RESULT);
        GL15C.glDeleteQueries(query);
        loggedFirstDrawSamples = true;

        Limlib.LOGGER.info(
            "[specialmodels/sodium] first special model draw samples passed: renderType={}, shader={}, samples={}",
            renderType,
            shader.getName(),
            samplesPassed);
    }

    public static void clear() {
        PendingSectionMeshes pending;

        while ((pending = pendingSections.poll()) != null) {
            pending.close();
        }

        uploadedSections.values().forEach(UploadedSectionMeshes::close);
        uploadedSections.clear();
    }

    private static void uploadPending() {
        PendingSectionMeshes pending;

        while ((pending = pendingSections.poll()) != null) {
            UploadedSectionMeshes previous = uploadedSections.remove(pending.sectionKey());

            if (previous != null) {
                previous.close();
            }

            if (pending.meshes().isEmpty()) {
                continue;
            }

            uploadedSections.put(pending.sectionKey(), pending.upload());
        }
    }

    public record PendingMeshBuilder(ByteBufferBuilder byteBuffer, BufferBuilder buffer) {

        @Nullable
        private PendingMesh build() {
            MeshData meshData = this.buffer.build();

            if (meshData == null) {
                this.byteBuffer.close();
                return null;
            }

            return new PendingMesh(meshData, this.byteBuffer);
        }
    }

    private record PendingMesh(MeshData meshData, ByteBufferBuilder byteBuffer) {

        private VertexBuffer upload() {
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            vertexBuffer.bind();

            try {
                vertexBuffer.upload(this.meshData);
            } finally {
                VertexBuffer.unbind();
                this.byteBuffer.close();
            }

            return vertexBuffer;
        }

        private void close() {
            try {
                this.meshData.close();
            } finally {
                this.byteBuffer.close();
            }
        }
    }

    private record PendingSectionMeshes(long sectionKey, Map<RenderType, PendingMesh> meshes) {

        private UploadedSectionMeshes upload() {
            Map<RenderType, VertexBuffer> uploaded = new LinkedHashMap<>();

            try {
                for (Map.Entry<RenderType, PendingMesh> entry : this.meshes.entrySet()) {
                    uploaded.put(entry.getKey(), entry.getValue().upload());
                }
            } catch (RuntimeException exception) {
                uploaded.values().forEach(VertexBuffer::close);
                throw exception;
            }

            return new UploadedSectionMeshes(uploaded);
        }

        private void close() {
            this.meshes.values().forEach(PendingMesh::close);
        }
    }

    private record UploadedSectionMeshes(Map<RenderType, VertexBuffer> meshes) {

        private void close() {
            this.meshes.values().forEach(VertexBuffer::close);
        }
    }

    private static final class SpecialModelDrawState implements AutoCloseable {
        private static final SpecialModelDrawState NOOP = new SpecialModelDrawState(false, new boolean[0], new ColorMask[0]);
        private final boolean active;
        private final boolean[] blendEnabled;
        private final ColorMask[] colorMasks;

        static SpecialModelDrawState apply() {
            if (!IrisCompat.isShaderPackInUse()) {
                return NOOP;
            }

            boolean[] blendEnabled = new boolean[4];
            ColorMask[] colorMasks = new ColorMask[4];

            for (int index = 0; index < 4; index++) {
                blendEnabled[index] = GL30C.glIsEnabledi(GL11C.GL_BLEND, index);
                colorMasks[index] = ColorMask.capture(index);
                GL30C.glColorMaski(index, true, true, true, true);
            }

            GL30C.glEnablei(GL11C.GL_BLEND, 0);
            GL30C.glDisablei(GL11C.GL_BLEND, 1);
            GL30C.glDisablei(GL11C.GL_BLEND, 2);
            GL30C.glDisablei(GL11C.GL_BLEND, 3);

            return new SpecialModelDrawState(true, blendEnabled, colorMasks);
        }

        private SpecialModelDrawState(boolean active, boolean[] blendEnabled, ColorMask[] colorMasks) {
            this.active = active;
            this.blendEnabled = blendEnabled;
            this.colorMasks = colorMasks;
        }

        @Override
        public void close() {
            if (!this.active) {
                return;
            }

            for (int index = 0; index < 4; index++) {
                if (this.blendEnabled[index]) {
                    GL30C.glEnablei(GL11C.GL_BLEND, index);
                } else {
                    GL30C.glDisablei(GL11C.GL_BLEND, index);
                }

                this.colorMasks[index].restore(index);
            }
        }
    }

    private record ColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        static ColorMask capture(int index) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                ByteBuffer values = stack.malloc(4);
                GL30C.glGetBooleani_v(GL11C.GL_COLOR_WRITEMASK, index, values);
                return new ColorMask(values.get(0) != 0, values.get(1) != 0, values.get(2) != 0, values.get(3) != 0);
            }
        }

        void restore(int index) {
            GL30C.glColorMaski(index, this.red, this.green, this.blue, this.alpha);
        }
    }

    private SodiumSpecialModelMeshRegistry() {
    }
}
