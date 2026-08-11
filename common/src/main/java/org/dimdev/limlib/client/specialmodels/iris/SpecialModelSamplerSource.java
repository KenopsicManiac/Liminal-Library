package org.dimdev.limlib.client.specialmodels.iris;

import java.util.Locale;
import java.util.Optional;

public enum SpecialModelSamplerSource {
    BLOCK_ATLAS("block_atlas"),
    LIGHTMAP("lightmap"),
    LIMLIB_CALLBACK("limlib_callback"),
    IRIS_GBUFFER("iris_gbuffer"),
    IRIS_DEPTH("iris_depth"),
    SHADERPACK("shaderpack");

    private final String serializedName;

    SpecialModelSamplerSource(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public static Optional<SpecialModelSamplerSource> bySerializedName(String serializedName) {
        String normalized = serializedName.toLowerCase(Locale.ROOT);

        for (SpecialModelSamplerSource source : values()) {
            if (source.serializedName.equals(normalized)) {
                return Optional.of(source);
            }
        }

        return Optional.empty();
    }
}
