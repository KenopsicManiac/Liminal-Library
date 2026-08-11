package org.dimdev.limlib.client.specialmodels;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.limlib.client.specialmodels.compat.iris.IrisCompat;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelIrisOptions;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class SpecialModelShaderRegistry {

    private static final Map<ResourceLocation, SpecialModelShaderOptions> OPTIONS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, ShaderInstance> BUNDLED_SHADERS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, ShaderpackShader> SHADERPACK_SHADERS = new ConcurrentHashMap<>();

    public static void register(ResourceLocation rendererId, ResourceLocation shaderId, @Nullable ShaderCallback setupCallback) {
        register(rendererId, shaderId, DefaultVertexFormat.BLOCK, setupCallback);
    }

    public static void register(ResourceLocation rendererId, ResourceLocation shaderId, @Nullable ShaderCallback setupCallback,
                                SpecialModelIrisOptions irisOptions) {
        register(rendererId, shaderId, DefaultVertexFormat.BLOCK, setupCallback, irisOptions);
    }

    public static synchronized void register(ResourceLocation rendererId, ResourceLocation shaderId, VertexFormat vertexFormat, @Nullable ShaderCallback setupCallback) {
        register(rendererId, shaderId, vertexFormat, setupCallback, SpecialModelIrisOptions.disabled());
    }

    public static synchronized void register(ResourceLocation rendererId, ResourceLocation shaderId, VertexFormat vertexFormat,
                                             @Nullable ShaderCallback setupCallback, SpecialModelIrisOptions irisOptions) {
        register(new SpecialModelShaderOptions(rendererId, shaderId, vertexFormat, setupCallback, irisOptions));
    }

    public static synchronized void register(SpecialModelShaderOptions options) {
        SpecialModelShaderOptions existing = OPTIONS.get(options.rendererId());

        if (existing != null) {
            if (!existing.sameDefinition(options)) {
                throw new IllegalArgumentException("Special model shader renderer is already registered: " + options.rendererId());
            }

            return;
        }

        OPTIONS.put(options.rendererId(), options);
    }

    public static synchronized boolean isRegistered(ResourceLocation rendererId) {
        return OPTIONS.containsKey(rendererId);
    }

    public static synchronized VertexFormat getVertexFormat(ResourceLocation rendererId) {
        SpecialModelShaderOptions options = OPTIONS.get(rendererId);
        return options != null ? options.vertexFormat() : DefaultVertexFormat.BLOCK;
    }

    public static synchronized SpecialModelIrisOptions getIrisOptions(ResourceLocation rendererId) {
        SpecialModelShaderOptions options = OPTIONS.get(rendererId);
        return options != null ? options.irisOptions() : SpecialModelIrisOptions.disabled();
    }

    public static synchronized int appendOverlayState(ResourceLocation rendererId, BlockAndTintGetter level, BlockPos pos, BlockState state,
                                                      BakedModel model, long modelSeed) {
        SpecialModelShaderOptions options = OPTIONS.get(rendererId);

        if (options == null || options.setupCallback() == null) {
            return OverlayTexture.NO_OVERLAY;
        }

        return options.setupCallback().appendOverlayState(level, pos, state, model, modelSeed);
    }

    @Nullable
    public static ShaderInstance getShader(ResourceLocation rendererId) {
        if (IrisCompat.isShaderPackInUse()) {
            ShaderpackShader shaderpackShader = SHADERPACK_SHADERS.get(rendererId);

            if (shaderpackShader != null && shaderpackShader.packName().equals(IrisCompat.currentShaderPackName())) {
                return shaderpackShader.shader();
            }
        }

        return BUNDLED_SHADERS.get(rendererId);
    }

    public static synchronized Collection<SpecialModelShaderOptions> registeredOptions() {
        return List.copyOf(OPTIONS.values());
    }

    public static synchronized Optional<SpecialModelShaderOptions> getOptionsByShaderId(ResourceLocation shaderId) {
        return OPTIONS.values().stream()
            .filter(options -> options.shaderId().equals(shaderId))
            .findFirst();
    }

    public static void registerCoreShaders(TriConsumer<ResourceLocation, VertexFormat, Consumer<ShaderInstance>> context) throws IOException {
        registerCoreShadersThrowing((shaderId, vertexFormat, loadCallback) -> context.accept(shaderId, vertexFormat, loadCallback));
    }

    public static void registerCoreShadersThrowing(ThrowingShaderRegistrationContext context) throws IOException {
        BUNDLED_SHADERS.clear();
        SHADERPACK_SHADERS.clear();

        for (SpecialModelShaderOptions options : registeredOptions()) {
            context.accept(options.shaderId(), options.vertexFormat(), shader -> {
                configureShader(options, shader);
                BUNDLED_SHADERS.put(options.rendererId(), shader);
            });
        }
    }

    public static synchronized void registerShaderpackShader(ResourceLocation shaderId, String packName, ShaderInstance shader) {
        getOptionsByShaderId(shaderId).ifPresent(options -> registerShaderpackShader(options, packName, shader));
    }

    public static synchronized void registerShaderpackShader(SpecialModelShaderOptions options, String packName, ShaderInstance shader) {
        if (packName == null || packName.isBlank()) {
            return;
        }

        configureShader(options, shader);
        SHADERPACK_SHADERS.put(options.rendererId(), new ShaderpackShader(packName, shader));
    }

    private static void configureShader(SpecialModelShaderOptions options, ShaderInstance shader) {
        if (options.setupCallback() != null) {
            ((ShaderInstanceExt) shader).addUniformSetCallback(options.setupCallback()::setup);
        }
    }

    @FunctionalInterface
    public interface ThrowingShaderRegistrationContext {
        void accept(ResourceLocation shaderId, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) throws IOException;
    }

    public record SpecialModelShaderOptions(ResourceLocation rendererId, ResourceLocation shaderId, VertexFormat vertexFormat,
                                            @Nullable ShaderCallback setupCallback, SpecialModelIrisOptions irisOptions) {

        public SpecialModelShaderOptions(ResourceLocation rendererId, ResourceLocation shaderId, VertexFormat vertexFormat,
                                         @Nullable ShaderCallback setupCallback) {
            this(rendererId, shaderId, vertexFormat, setupCallback, SpecialModelIrisOptions.disabled());
        }

        public SpecialModelShaderOptions {
            Objects.requireNonNull(rendererId, "rendererId");
            Objects.requireNonNull(shaderId, "shaderId");
            Objects.requireNonNull(vertexFormat, "vertexFormat");
            Objects.requireNonNull(irisOptions, "irisOptions");
        }

        private boolean sameDefinition(SpecialModelShaderOptions other) {
            return this.rendererId.equals(other.rendererId)
                && this.shaderId.equals(other.shaderId)
                && this.vertexFormat.equals(other.vertexFormat)
                && this.setupCallback == other.setupCallback
                && this.irisOptions.equals(other.irisOptions);
        }
    }

    private record ShaderpackShader(String packName, ShaderInstance shader) {
    }

    private SpecialModelShaderRegistry() {
    }
}
