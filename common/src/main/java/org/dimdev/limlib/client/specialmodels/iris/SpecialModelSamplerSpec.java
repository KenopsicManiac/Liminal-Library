package org.dimdev.limlib.client.specialmodels.iris;

import java.util.Objects;

public record SpecialModelSamplerSpec(
    String name,
    boolean required,
    SpecialModelSamplerSource source
) {

    public SpecialModelSamplerSpec {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
    }

    public static SpecialModelSamplerSpec required(String name, SpecialModelSamplerSource source) {
        return new SpecialModelSamplerSpec(name, true, source);
    }

    public static SpecialModelSamplerSpec optional(String name, SpecialModelSamplerSource source) {
        return new SpecialModelSamplerSpec(name, false, source);
    }
}
