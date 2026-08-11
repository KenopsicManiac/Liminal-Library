package org.dimdev.limlib.client.specialmodels.iris;

import java.util.Locale;
import java.util.Optional;

public enum SpecialModelBlendMode {
    NONE("none"),
    ALPHA("alpha"),
    ADDITIVE("additive"),
    MULTIPLY("multiply"),
    SHADERPACK("shaderpack");

    private final String serializedName;

    SpecialModelBlendMode(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public static Optional<SpecialModelBlendMode> bySerializedName(String serializedName) {
        String normalized = serializedName.toLowerCase(Locale.ROOT);

        for (SpecialModelBlendMode mode : values()) {
            if (mode.serializedName.equals(normalized)) {
                return Optional.of(mode);
            }
        }

        return Optional.empty();
    }
}
