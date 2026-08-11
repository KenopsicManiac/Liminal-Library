package org.dimdev.limlib.client.specialmodels.iris;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;

public record SpecialModelShaderpackDefinition(
    int metadataVersion,
    ResourceLocation effectId,
    SpecialModelIrisStage stage,
    ResourceLocation vertexFormatId,
    SpecialModelOutputContract outputContract,
    SpecialModelIrisFallback fallback,
    List<SpecialModelUniformSpec> uniforms,
    List<SpecialModelSamplerSpec> samplers
) {
    public static final int CURRENT_METADATA_VERSION = 1;

    public SpecialModelShaderpackDefinition {
        Objects.requireNonNull(effectId, "effectId");
        Objects.requireNonNull(stage, "stage");
        Objects.requireNonNull(vertexFormatId, "vertexFormatId");
        Objects.requireNonNull(outputContract, "outputContract");
        Objects.requireNonNull(fallback, "fallback");
        uniforms = List.copyOf(uniforms);
        samplers = List.copyOf(samplers);
    }

    public static Builder builder(ResourceLocation effectId) {
        return new Builder(effectId);
    }

    public static final class Builder {
        private int metadataVersion = CURRENT_METADATA_VERSION;
        private final ResourceLocation effectId;
        private SpecialModelIrisStage stage = SpecialModelIrisStage.TERRAIN_TRANSLUCENT;
        private ResourceLocation vertexFormatId = ResourceLocation.withDefaultNamespace("block");
        private SpecialModelOutputContract outputContract = SpecialModelOutputContract.singleColor();
        private SpecialModelIrisFallback fallback = SpecialModelIrisFallback.VANILLA_SPECIAL_SHADER;
        private List<SpecialModelUniformSpec> uniforms = List.of();
        private List<SpecialModelSamplerSpec> samplers = List.of();

        private Builder(ResourceLocation effectId) {
            this.effectId = Objects.requireNonNull(effectId, "effectId");
        }

        public Builder metadataVersion(int metadataVersion) {
            this.metadataVersion = metadataVersion;
            return this;
        }

        public Builder stage(SpecialModelIrisStage stage) {
            this.stage = Objects.requireNonNull(stage, "stage");
            return this;
        }

        public Builder vertexFormatId(ResourceLocation vertexFormatId) {
            this.vertexFormatId = Objects.requireNonNull(vertexFormatId, "vertexFormatId");
            return this;
        }

        public Builder outputContract(SpecialModelOutputContract outputContract) {
            this.outputContract = Objects.requireNonNull(outputContract, "outputContract");
            return this;
        }

        public Builder fallback(SpecialModelIrisFallback fallback) {
            this.fallback = Objects.requireNonNull(fallback, "fallback");
            return this;
        }

        public Builder uniforms(List<SpecialModelUniformSpec> uniforms) {
            this.uniforms = List.copyOf(uniforms);
            return this;
        }

        public Builder samplers(List<SpecialModelSamplerSpec> samplers) {
            this.samplers = List.copyOf(samplers);
            return this;
        }

        public SpecialModelShaderpackDefinition build() {
            return new SpecialModelShaderpackDefinition(
                this.metadataVersion,
                this.effectId,
                this.stage,
                this.vertexFormatId,
                this.outputContract,
                this.fallback,
                this.uniforms,
                this.samplers);
        }
    }
}
