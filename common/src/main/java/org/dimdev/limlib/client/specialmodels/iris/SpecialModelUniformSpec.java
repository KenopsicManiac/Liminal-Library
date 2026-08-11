package org.dimdev.limlib.client.specialmodels.iris;

import java.util.Objects;

public record SpecialModelUniformSpec(
    String name,
    SpecialModelValueType type,
    boolean required,
    SpecialModelUniformSource source
) {

    public SpecialModelUniformSpec {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(source, "source");
    }

    public static SpecialModelUniformSpec required(String name, SpecialModelValueType type, SpecialModelUniformSource source) {
        return new SpecialModelUniformSpec(name, type, true, source);
    }

    public static SpecialModelUniformSpec optional(String name, SpecialModelValueType type, SpecialModelUniformSource source) {
        return new SpecialModelUniformSpec(name, type, false, source);
    }
}
