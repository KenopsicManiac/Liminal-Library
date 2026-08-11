package org.dimdev.limlib.client.specialmodels.iris;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class SpecialModelIrisMetadataPaths {
    public static final String ROOT = "shaders/limlib/special_models";
    public static final String METADATA_EXTENSION = ".json";
    public static final String VERTEX_SHADER_EXTENSION = ".vsh";
    public static final String FRAGMENT_SHADER_EXTENSION = ".fsh";
    public static final String GEOMETRY_SHADER_EXTENSION = ".gsh";

    public static String metadataPath(ResourceLocation effectId) {
        return path(effectId, METADATA_EXTENSION);
    }

    public static String vertexShaderPath(ResourceLocation effectId) {
        return path(effectId, VERTEX_SHADER_EXTENSION);
    }

    public static String fragmentShaderPath(ResourceLocation effectId) {
        return path(effectId, FRAGMENT_SHADER_EXTENSION);
    }

    public static String geometryShaderPath(ResourceLocation effectId) {
        return path(effectId, GEOMETRY_SHADER_EXTENSION);
    }

    public static String path(ResourceLocation effectId, String extension) {
        Objects.requireNonNull(effectId, "effectId");
        Objects.requireNonNull(extension, "extension");

        if (!extension.startsWith(".")) {
            throw new IllegalArgumentException("extension must start with '.'");
        }

        return ROOT + "/" + effectId.getNamespace() + "/" + effectId.getPath() + extension;
    }

    private SpecialModelIrisMetadataPaths() {
    }
}
