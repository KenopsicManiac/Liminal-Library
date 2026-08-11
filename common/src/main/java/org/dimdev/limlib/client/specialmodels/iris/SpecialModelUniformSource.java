package org.dimdev.limlib.client.specialmodels.iris;

import java.util.Locale;
import java.util.Optional;

public enum SpecialModelUniformSource {
    LIMLIB_CALLBACK("limlib_callback"),
    MINECRAFT_DEFAULT("minecraft_default"),
    IRIS_FRAME("iris_frame"),
    SHADERPACK("shaderpack");

    private final String serializedName;

    SpecialModelUniformSource(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public static Optional<SpecialModelUniformSource> bySerializedName(String serializedName) {
        String normalized = serializedName.toLowerCase(Locale.ROOT);

        for (SpecialModelUniformSource source : values()) {
            if (source.serializedName.equals(normalized)) {
                return Optional.of(source);
            }
        }

        return Optional.empty();
    }
}
