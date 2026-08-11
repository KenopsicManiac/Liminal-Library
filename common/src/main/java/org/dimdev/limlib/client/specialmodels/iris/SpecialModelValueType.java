package org.dimdev.limlib.client.specialmodels.iris;

import java.util.Locale;
import java.util.Optional;

public enum SpecialModelValueType {
    BOOL("bool"),
    INT("int"),
    FLOAT("float"),
    VEC2("vec2"),
    VEC3("vec3"),
    VEC4("vec4"),
    IVEC2("ivec2"),
    IVEC3("ivec3"),
    IVEC4("ivec4"),
    MAT2("mat2"),
    MAT3("mat3"),
    MAT4("mat4"),
    COLOR("color"),
    UV("uv"),
    UV1("uv1"),
    UV2("uv2"),
    NORMAL("normal");

    private final String serializedName;

    SpecialModelValueType(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public static Optional<SpecialModelValueType> bySerializedName(String serializedName) {
        String normalized = serializedName.toLowerCase(Locale.ROOT);

        for (SpecialModelValueType type : values()) {
            if (type.serializedName.equals(normalized)) {
                return Optional.of(type);
            }
        }

        return Optional.empty();
    }
}
