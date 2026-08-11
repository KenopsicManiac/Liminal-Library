package org.dimdev.limlib.client.specialmodels.iris;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SpecialModelIrisValidator {

    public static SpecialModelIrisValidationResult validate(SpecialModelIrisOptions options, SpecialModelShaderpackDefinition definition) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (!options.enabled()) {
            errors.add("special model effect has no Iris shaderpack contract");
        }

        if (definition.metadataVersion() != SpecialModelShaderpackDefinition.CURRENT_METADATA_VERSION) {
            errors.add("unsupported LimLib special model metadata version: " + definition.metadataVersion());
        }

        if (!options.effectId().equals(definition.effectId())) {
            errors.add("effect id mismatch: expected " + options.effectId() + ", found " + definition.effectId());
        }

        if (options.stage() != definition.stage()) {
            errors.add("stage mismatch for " + options.effectId() + ": expected " + options.stage().serializedName()
                + ", found " + definition.stage().serializedName());
        }

        if (!options.vertexContract().vertexFormatId().equals(definition.vertexFormatId())) {
            errors.add("vertex format mismatch for " + options.effectId() + ": expected "
                + options.vertexContract().vertexFormatId() + ", found " + definition.vertexFormatId());
        }

        validateOutput(options, definition, errors);
        validateUniforms(options, definition, errors, warnings);
        validateSamplers(options, definition, errors, warnings);

        return SpecialModelIrisValidationResult.of(errors, warnings);
    }

    private static void validateOutput(SpecialModelIrisOptions options, SpecialModelShaderpackDefinition definition, List<String> errors) {
        SpecialModelOutputContract expected = options.outputContract();
        SpecialModelOutputContract actual = definition.outputContract();

        if (actual.fragmentOutputCount() < expected.fragmentOutputCount()) {
            errors.add("fragment output count too small for " + options.effectId() + ": expected at least "
                + expected.fragmentOutputCount() + ", found " + actual.fragmentOutputCount());
        }

        if (actual.defaultDrawBuffers().length < actual.fragmentOutputCount()) {
            errors.add("draw buffer count is smaller than fragment output count for " + options.effectId());
        }

        if (expected.writesDepth() && !actual.writesDepth()) {
            errors.add("shaderpack definition does not declare required depth writes for " + options.effectId());
        }

        if (expected.requiresPreTranslucentDepth() && !actual.requiresPreTranslucentDepth()) {
            errors.add("shaderpack definition does not declare required pre-translucent depth for " + options.effectId());
        }

        if (expected.requiresMainDepth() && !actual.requiresMainDepth()) {
            errors.add("shaderpack definition does not declare required main depth for " + options.effectId());
        }
    }

    private static void validateUniforms(SpecialModelIrisOptions options, SpecialModelShaderpackDefinition definition,
                                         List<String> errors, List<String> warnings) {
        Map<String, SpecialModelUniformSpec> actual = uniformsByName(definition.uniforms());

        for (SpecialModelUniformSpec expected : options.uniforms()) {
            SpecialModelUniformSpec found = actual.get(expected.name());

            if (found == null) {
                if (expected.required()) {
                    errors.add("missing required uniform for " + options.effectId() + ": " + expected.name());
                } else {
                    warnings.add("missing optional uniform for " + options.effectId() + ": " + expected.name());
                }

                continue;
            }

            if (found.type() != expected.type()) {
                errors.add("uniform type mismatch for " + options.effectId() + "." + expected.name()
                    + ": expected " + expected.type().serializedName() + ", found " + found.type().serializedName());
            }

            if (found.source() != expected.source()) {
                errors.add("uniform source mismatch for " + options.effectId() + "." + expected.name()
                    + ": expected " + expected.source().serializedName() + ", found " + found.source().serializedName());
            }
        }
    }

    private static void validateSamplers(SpecialModelIrisOptions options, SpecialModelShaderpackDefinition definition,
                                         List<String> errors, List<String> warnings) {
        Map<String, SpecialModelSamplerSpec> actual = samplersByName(definition.samplers());

        for (SpecialModelSamplerSpec expected : options.samplers()) {
            SpecialModelSamplerSpec found = actual.get(expected.name());

            if (found == null) {
                if (expected.required()) {
                    errors.add("missing required sampler for " + options.effectId() + ": " + expected.name());
                } else {
                    warnings.add("missing optional sampler for " + options.effectId() + ": " + expected.name());
                }

                continue;
            }

            if (found.source() != expected.source()) {
                errors.add("sampler source mismatch for " + options.effectId() + "." + expected.name()
                    + ": expected " + expected.source().serializedName() + ", found " + found.source().serializedName());
            }
        }
    }

    private static Map<String, SpecialModelUniformSpec> uniformsByName(List<SpecialModelUniformSpec> uniforms) {
        Map<String, SpecialModelUniformSpec> byName = new LinkedHashMap<>();

        for (SpecialModelUniformSpec uniform : uniforms) {
            byName.put(uniform.name(), uniform);
        }

        return byName;
    }

    private static Map<String, SpecialModelSamplerSpec> samplersByName(List<SpecialModelSamplerSpec> samplers) {
        Map<String, SpecialModelSamplerSpec> byName = new LinkedHashMap<>();

        for (SpecialModelSamplerSpec sampler : samplers) {
            byName.put(sampler.name(), sampler);
        }

        return byName;
    }

    private SpecialModelIrisValidator() {
    }
}
