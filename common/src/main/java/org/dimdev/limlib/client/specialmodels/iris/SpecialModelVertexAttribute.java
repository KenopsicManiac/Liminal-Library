package org.dimdev.limlib.client.specialmodels.iris;

import java.util.Objects;

public record SpecialModelVertexAttribute(String name, SpecialModelValueType type, boolean required) {

    public SpecialModelVertexAttribute {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
    }

    public static SpecialModelVertexAttribute required(String name, SpecialModelValueType type) {
        return new SpecialModelVertexAttribute(name, type, true);
    }

    public static SpecialModelVertexAttribute optional(String name, SpecialModelValueType type) {
        return new SpecialModelVertexAttribute(name, type, false);
    }
}
