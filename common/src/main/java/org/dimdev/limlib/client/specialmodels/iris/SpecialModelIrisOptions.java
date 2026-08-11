package org.dimdev.limlib.client.specialmodels.iris;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record SpecialModelIrisOptions(
    boolean enabled,
    ResourceLocation effectId,
    SpecialModelIrisStage stage,
    SpecialModelIrisFallback fallback,
    SpecialModelVertexContract vertexContract,
    SpecialModelOutputContract outputContract,
    List<SpecialModelUniformSpec> uniforms,
    List<SpecialModelSamplerSpec> samplers
) {
    private static final ResourceLocation DISABLED_EFFECT_ID = ResourceLocation.parse("limlib:disabled");
    private static final SpecialModelIrisOptions DISABLED = new SpecialModelIrisOptions(
        false,
        DISABLED_EFFECT_ID,
        SpecialModelIrisStage.TERRAIN_TRANSLUCENT,
        SpecialModelIrisFallback.VANILLA_SPECIAL_SHADER,
        SpecialModelVertexContract.block(),
        SpecialModelOutputContract.singleColor(),
        List.of(),
        List.of());

    public SpecialModelIrisOptions {
        Objects.requireNonNull(effectId, "effectId");
        Objects.requireNonNull(stage, "stage");
        Objects.requireNonNull(fallback, "fallback");
        Objects.requireNonNull(vertexContract, "vertexContract");
        Objects.requireNonNull(outputContract, "outputContract");
        uniforms = List.copyOf(uniforms);
        samplers = List.copyOf(samplers);
    }

    public static SpecialModelIrisOptions disabled() {
        return DISABLED;
    }

    public static Builder builder(ResourceLocation effectId) {
        return new Builder(effectId);
    }

    public boolean hasShaderpackContract() {
        return this.enabled;
    }

    public Builder toBuilder() {
        Builder builder = new Builder(this.effectId);
        builder.enabled = this.enabled;
        builder.stage = this.stage;
        builder.fallback = this.fallback;
        builder.vertexContract = this.vertexContract;
        builder.outputContract = this.outputContract;
        builder.uniforms.addAll(this.uniforms);
        builder.samplers.addAll(this.samplers);
        return builder;
    }

    public static final class Builder {
        private boolean enabled = true;
        private final ResourceLocation effectId;
        private SpecialModelIrisStage stage = SpecialModelIrisStage.TERRAIN_TRANSLUCENT;
        private SpecialModelIrisFallback fallback = SpecialModelIrisFallback.VANILLA_SPECIAL_SHADER;
        private SpecialModelVertexContract vertexContract = SpecialModelVertexContract.block();
        private SpecialModelOutputContract outputContract = SpecialModelOutputContract.singleColor();
        private final List<SpecialModelUniformSpec> uniforms = new ArrayList<>();
        private final List<SpecialModelSamplerSpec> samplers = new ArrayList<>();

        private Builder(ResourceLocation effectId) {
            this.effectId = Objects.requireNonNull(effectId, "effectId");
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder stage(SpecialModelIrisStage stage) {
            this.stage = Objects.requireNonNull(stage, "stage");
            return this;
        }

        public Builder fallback(SpecialModelIrisFallback fallback) {
            this.fallback = Objects.requireNonNull(fallback, "fallback");
            return this;
        }

        public Builder vertexContract(SpecialModelVertexContract vertexContract) {
            this.vertexContract = Objects.requireNonNull(vertexContract, "vertexContract");
            return this;
        }

        public Builder outputContract(SpecialModelOutputContract outputContract) {
            this.outputContract = Objects.requireNonNull(outputContract, "outputContract");
            return this;
        }

        public Builder uniform(SpecialModelUniformSpec uniform) {
            this.uniforms.add(Objects.requireNonNull(uniform, "uniform"));
            return this;
        }

        public Builder requiredUniform(String name, SpecialModelValueType type, SpecialModelUniformSource source) {
            return this.uniform(SpecialModelUniformSpec.required(name, type, source));
        }

        public Builder optionalUniform(String name, SpecialModelValueType type, SpecialModelUniformSource source) {
            return this.uniform(SpecialModelUniformSpec.optional(name, type, source));
        }

        public Builder sampler(SpecialModelSamplerSpec sampler) {
            this.samplers.add(Objects.requireNonNull(sampler, "sampler"));
            return this;
        }

        public Builder requiredSampler(String name, SpecialModelSamplerSource source) {
            return this.sampler(SpecialModelSamplerSpec.required(name, source));
        }

        public Builder optionalSampler(String name, SpecialModelSamplerSource source) {
            return this.sampler(SpecialModelSamplerSpec.optional(name, source));
        }

        public SpecialModelIrisOptions build() {
            return new SpecialModelIrisOptions(
                this.enabled,
                this.effectId,
                this.stage,
                this.fallback,
                this.vertexContract,
                this.outputContract,
                this.uniforms,
                this.samplers);
        }
    }
}
