package org.dimdev.limlib.client.specialmodels.iris;

import java.util.List;

public record SpecialModelIrisValidationResult(boolean valid, List<String> errors, List<String> warnings) {

    public SpecialModelIrisValidationResult {
        errors = List.copyOf(errors);
        warnings = List.copyOf(warnings);

        if (valid && !errors.isEmpty()) {
            throw new IllegalArgumentException("valid results cannot contain errors");
        }
    }

    public static SpecialModelIrisValidationResult success() {
        return new SpecialModelIrisValidationResult(true, List.of(), List.of());
    }

    public static SpecialModelIrisValidationResult invalid(List<String> errors) {
        return new SpecialModelIrisValidationResult(false, errors, List.of());
    }

    public static SpecialModelIrisValidationResult of(List<String> errors, List<String> warnings) {
        return new SpecialModelIrisValidationResult(errors.isEmpty(), errors, warnings);
    }
}
