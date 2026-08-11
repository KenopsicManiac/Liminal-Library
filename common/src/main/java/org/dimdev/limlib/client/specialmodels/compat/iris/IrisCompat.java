package org.dimdev.limlib.client.specialmodels.compat.iris;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.dimdev.limlib.client.specialmodels.SpecialModelShaderRegistry;
import org.dimdev.limlib.client.specialmodels.SpecialModelShaderRegistry.SpecialModelShaderOptions;
import org.dimdev.limlib.impl.Limlib;

import java.io.IOException;
import java.util.Optional;

public final class IrisCompat {

    public static boolean shouldDisableSpecialModelRenderTypes() {
        return false;
    }

    public static boolean isShaderPackInUse() {
        if (!Limlib.isModLoaded("iris")) {
            return false;
        }

        try {
            return IrisApiProxy.isShaderPackInUse();
        } catch (LinkageError | RuntimeException exception) {
            return false;
        }
    }

    public static boolean isShaderPackSelectedAndEnabled() {
        if (!Limlib.isModLoaded("iris")) {
            return false;
        }

        try {
            return IrisApiProxy.areShadersEnabled() && !IrisSpecialModelShaderpackMetadataLoader.selectedPackName().isBlank();
        } catch (LinkageError | RuntimeException exception) {
            return false;
        }
    }

    public static boolean hasSelectedShaderPack() {
        if (!Limlib.isModLoaded("iris")) {
            return false;
        }

        try {
            return !IrisSpecialModelShaderpackMetadataLoader.selectedPackName().isBlank();
        } catch (LinkageError | RuntimeException exception) {
            return false;
        }
    }

    public static String currentShaderPackName() {
        if (!Limlib.isModLoaded("iris")) {
            return "";
        }

        try {
            return IrisSpecialModelShaderpackMetadataLoader.currentPackName();
        } catch (LinkageError | RuntimeException exception) {
            return "";
        }
    }

    public static String selectedShaderPackName() {
        if (!Limlib.isModLoaded("iris")) {
            return "";
        }

        try {
            return IrisSpecialModelShaderpackMetadataLoader.selectedPackName();
        } catch (LinkageError | RuntimeException exception) {
            return "";
        }
    }

    public static void withTerrainTranslucentPhase(Runnable runnable) {
        if (!isShaderPackInUse()) {
            runnable.run();
            return;
        }

        validateShaderpackSpecialModels();

        try {
            IrisPipelineProxy.withTerrainTranslucentPhase(runnable);
        } catch (LinkageError | RuntimeException exception) {
            runnable.run();
        }
    }

    public static void bindSpecialModelTerrainFramebuffer() {
        if (!isShaderPackInUse()) {
            return;
        }

        try {
            IrisPipelineProxy.bindTerrainTranslucentFramebuffer();
        } catch (LinkageError | RuntimeException exception) {
            // Rendering can still proceed through the currently bound target if Iris internals shift.
        }
    }

    public static void validateShaderpackSpecialModels() {
        if (!isShaderPackInUse()) {
            return;
        }

        try {
            IrisSpecialModelShaderpackValidationCache.refresh(SpecialModelShaderRegistry.registeredOptions());
        } catch (LinkageError | RuntimeException exception) {
            // Validation is passive. Rendering should continue through the existing fallback path if Iris internals shift.
        }
    }

    public static SpecialModelShaderProvider prepareSpecialModelShaderProvider(ResourceProvider provider, ResourceLocation shaderId) {
        if (!isShaderPackSelectedAndEnabled()) {
            return new SpecialModelShaderProvider(provider, false);
        }

        return prepareSpecialModelShaderpackProvider(provider, shaderId)
            .orElseGet(() -> new SpecialModelShaderProvider(provider, false));
    }

    public static Optional<SpecialModelShaderProvider> prepareSpecialModelShaderpackProvider(ResourceProvider provider, ResourceLocation shaderId) {
        Optional<SpecialModelShaderOptions> options = SpecialModelShaderRegistry.getOptionsByShaderId(shaderId);

        if (options.isEmpty() || !hasSelectedShaderPack()) {
            return Optional.empty();
        }

        try {
            String packName = selectedShaderPackName();
            IrisSpecialModelShaderpackMetadataLoader.ShaderpackFiles shaderpackFiles =
                IrisSpecialModelShaderpackMetadataLoader.findFiles(options.get().rendererId());

            if (!shaderpackFiles.hasShaderSources()) {
                return Optional.empty();
            }

            Limlib.LOGGER.info("[specialmodels/iris] loading shaderpack sources for {} from {}",
                options.get().rendererId(), packName);
            return Optional.of(new SpecialModelShaderProvider(new IrisSpecialModelShaderpackResourceProvider(provider, options.get()), true, packName));
        } catch (IOException | LinkageError | RuntimeException exception) {
            Limlib.LOGGER.warn("[specialmodels/iris] failed to prepare shaderpack sources for {}; using bundled core shader",
                options.get().rendererId(), exception);
            return Optional.empty();
        }
    }

    public static void logSpecialModelShaderpackFallback(ResourceLocation shaderId, Throwable exception) {
        ResourceLocation rendererId = SpecialModelShaderRegistry.getOptionsByShaderId(shaderId)
            .map(SpecialModelShaderOptions::rendererId)
            .orElse(shaderId);
        Limlib.LOGGER.warn("[specialmodels/iris] failed to compile shaderpack sources for {}; using bundled core shader",
            rendererId, exception);
    }

    public record SpecialModelShaderProvider(ResourceProvider provider, boolean shaderpackOverride, String packName) {
        public SpecialModelShaderProvider(ResourceProvider provider, boolean shaderpackOverride) {
            this(provider, shaderpackOverride, "");
        }
    }

    private IrisCompat() {
    }
}
