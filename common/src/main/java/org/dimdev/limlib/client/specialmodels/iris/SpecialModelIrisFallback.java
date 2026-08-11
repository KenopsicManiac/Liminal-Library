package org.dimdev.limlib.client.specialmodels.iris;

import java.util.Locale;
import java.util.Optional;

public enum SpecialModelIrisFallback {
    VANILLA_SPECIAL_SHADER("vanilla_special_shader"),
    DISABLE_SPECIAL_PASS("disable_special_pass"),
    REQUIRE_SHADERPACK_IMPLEMENTATION("require_shaderpack_implementation");

    private final String serializedName;

    SpecialModelIrisFallback(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public static Optional<SpecialModelIrisFallback> bySerializedName(String serializedName) {
        String normalized = serializedName.toLowerCase(Locale.ROOT);

        for (SpecialModelIrisFallback fallback : values()) {
            if (fallback.serializedName.equals(normalized)) {
                return Optional.of(fallback);
            }
        }

        return Optional.empty();
    }
}
