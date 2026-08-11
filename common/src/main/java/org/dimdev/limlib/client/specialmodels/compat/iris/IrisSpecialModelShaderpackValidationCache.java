package org.dimdev.limlib.client.specialmodels.compat.iris;

import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.limlib.client.specialmodels.SpecialModelShaderRegistry.SpecialModelShaderOptions;
import org.dimdev.limlib.client.specialmodels.compat.iris.IrisSpecialModelShaderpackMetadataLoader.ShaderpackFiles;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelIrisOptions;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelIrisFallback;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelIrisStage;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelIrisValidationResult;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelIrisValidator;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelOutputContract;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelShaderpackDefinition;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelVertexContract;
import org.dimdev.limlib.impl.Limlib;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

final class IrisSpecialModelShaderpackValidationCache {
    private static String lastSignature = "";
    private static Map<ResourceLocation, SpecialModelShaderpackDefinition> definitions = Map.of();
    private static Map<ResourceLocation, SpecialModelIrisValidationResult> validationResults = Map.of();

    static synchronized void refresh(Collection<SpecialModelShaderOptions> options) {
        String signature = signature(options);

        if (lastSignature.equals(signature)) {
            return;
        }

        lastSignature = signature;

        Map<ResourceLocation, SpecialModelShaderpackDefinition> loadedDefinitions = new LinkedHashMap<>();
        Map<ResourceLocation, SpecialModelIrisValidationResult> loadedResults = new LinkedHashMap<>();

        for (SpecialModelShaderOptions option : options) {
            loadAndValidate(option, loadedDefinitions, loadedResults);
        }

        definitions = Map.copyOf(loadedDefinitions);
        validationResults = Map.copyOf(loadedResults);
    }

    static synchronized Optional<SpecialModelShaderpackDefinition> definition(ResourceLocation effectId) {
        return Optional.ofNullable(definitions.get(effectId));
    }

    static synchronized Optional<SpecialModelIrisValidationResult> validationResult(ResourceLocation effectId) {
        return Optional.ofNullable(validationResults.get(effectId));
    }

    private static void loadAndValidate(SpecialModelShaderOptions option,
                                        Map<ResourceLocation, SpecialModelShaderpackDefinition> loadedDefinitions,
                                        Map<ResourceLocation, SpecialModelIrisValidationResult> loadedResults) {
        SpecialModelIrisOptions irisOptions = option.irisOptions();
        ResourceLocation effectId = effectId(option);

        try {
            ShaderpackFiles files = IrisSpecialModelShaderpackMetadataLoader.findFiles(effectId);

            if (!files.hasShaderSources()) {
                if (files.metadata()) {
                    Limlib.LOGGER.warn("[specialmodels/iris] metadata exists for {}, but required .vsh/.fsh sources are missing; using fallback",
                        effectId);
                }
                return;
            }

            SpecialModelIrisOptions effectiveOptions = effectiveOptions(option);
            Optional<SpecialModelShaderpackDefinition> definition = files.metadata()
                ? IrisSpecialModelShaderpackMetadataLoader.load(effectId)
                : Optional.empty();

            if (definition.isEmpty()) {
                loadedResults.put(effectId, SpecialModelIrisValidationResult.success());
                Limlib.LOGGER.info("[specialmodels/iris] found shaderpack sources for {}; using inferred LimLib special model contract",
                    effectId);
                return;
            }

            SpecialModelIrisValidationResult result = SpecialModelIrisValidator.validate(effectiveOptions, definition.get());
            loadedDefinitions.put(effectId, definition.get());
            loadedResults.put(effectId, result);

            if (result.valid()) {
                if (result.warnings().isEmpty()) {
                    Limlib.LOGGER.info("[specialmodels/iris] validated shaderpack metadata for {}", effectId);
                } else {
                    Limlib.LOGGER.info("[specialmodels/iris] validated shaderpack metadata for {} with warnings: {}",
                        effectId, String.join("; ", result.warnings()));
                }
            } else {
                Limlib.LOGGER.warn("[specialmodels/iris] invalid shaderpack metadata for {}; using {} fallback. Errors: {}",
                    effectId, effectiveOptions.fallback().serializedName(), String.join("; ", result.errors()));
            }
        } catch (IOException | JsonParseException exception) {
            Limlib.LOGGER.warn("[specialmodels/iris] failed to load shaderpack metadata for {}; using {} fallback",
                effectId, irisOptions.fallback().serializedName(), exception);
        }
    }

    private static String signature(Collection<SpecialModelShaderOptions> options) {
        StringBuilder signature = new StringBuilder(IrisSpecialModelShaderpackMetadataLoader.currentPackName()).append('\n');

        for (SpecialModelShaderOptions option : options) {
            signature
                .append(option.rendererId())
                .append('|')
                .append(option.shaderId())
                .append('|')
                .append(option.vertexFormat())
                .append('|')
                .append(option.irisOptions())
                .append('\n');
        }

        return signature.toString();
    }

    private static ResourceLocation effectId(SpecialModelShaderOptions option) {
        SpecialModelIrisOptions irisOptions = option.irisOptions();
        return irisOptions.enabled() ? irisOptions.effectId() : option.rendererId();
    }

    private static SpecialModelIrisOptions effectiveOptions(SpecialModelShaderOptions option) {
        SpecialModelIrisOptions irisOptions = option.irisOptions();

        if (irisOptions.enabled()) {
            return irisOptions;
        }

        return SpecialModelIrisOptions
            .builder(option.rendererId())
            .stage(SpecialModelIrisStage.TERRAIN_TRANSLUCENT)
            .fallback(SpecialModelIrisFallback.VANILLA_SPECIAL_SHADER)
            .vertexContract(SpecialModelVertexContract.infer(option.vertexFormat()))
            .outputContract(SpecialModelOutputContract.singleColor())
            .build();
    }

    private IrisSpecialModelShaderpackValidationCache() {
    }
}
