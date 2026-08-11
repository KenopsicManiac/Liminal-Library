package org.dimdev.limlib.client.specialmodels.iris;

import java.util.Locale;
import java.util.Optional;

public enum SpecialModelIrisStage {
    TERRAIN_TRANSLUCENT("terrain_translucent"),
    AFTER_TRANSLUCENT("after_translucent"),
    BEFORE_COMPOSITE("before_composite"),
    OVERLAY("overlay");

    private final String serializedName;

    SpecialModelIrisStage(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public static Optional<SpecialModelIrisStage> bySerializedName(String serializedName) {
        String normalized = serializedName.toLowerCase(Locale.ROOT);

        for (SpecialModelIrisStage stage : values()) {
            if (stage.serializedName.equals(normalized)) {
                return Optional.of(stage);
            }
        }

        return Optional.empty();
    }
}
