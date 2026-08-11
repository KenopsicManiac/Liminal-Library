package org.dimdev.limlib.client.specialmodels.iris;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record SpecialModelVertexContract(
    ResourceLocation vertexFormatId,
    VertexFormat vertexFormat,
    boolean requiresChunkOffset,
    List<SpecialModelVertexAttribute> attributes
) {
    private static final ResourceLocation BLOCK_VERTEX_FORMAT = ResourceLocation.withDefaultNamespace("block");
    private static final ResourceLocation NEW_ENTITY_VERTEX_FORMAT = ResourceLocation.withDefaultNamespace("new_entity");
    private static final ResourceLocation CUSTOM_VERTEX_FORMAT = ResourceLocation.parse("limlib:custom");

    public SpecialModelVertexContract {
        Objects.requireNonNull(vertexFormatId, "vertexFormatId");
        Objects.requireNonNull(vertexFormat, "vertexFormat");
        attributes = List.copyOf(attributes);
    }

    public static SpecialModelVertexContract block() {
        return new SpecialModelVertexContract(
            BLOCK_VERTEX_FORMAT,
            DefaultVertexFormat.BLOCK,
            true,
            List.of(
                SpecialModelVertexAttribute.required("Position", SpecialModelValueType.VEC3),
                SpecialModelVertexAttribute.required("Color", SpecialModelValueType.COLOR),
                SpecialModelVertexAttribute.required("UV0", SpecialModelValueType.UV),
                SpecialModelVertexAttribute.required("UV2", SpecialModelValueType.UV2)));
    }

    public static SpecialModelVertexContract newEntity() {
        return new SpecialModelVertexContract(
            NEW_ENTITY_VERTEX_FORMAT,
            DefaultVertexFormat.NEW_ENTITY,
            true,
            List.of(
                SpecialModelVertexAttribute.required("Position", SpecialModelValueType.VEC3),
                SpecialModelVertexAttribute.required("Color", SpecialModelValueType.COLOR),
                SpecialModelVertexAttribute.required("UV0", SpecialModelValueType.UV),
                SpecialModelVertexAttribute.required("UV1", SpecialModelValueType.UV1),
                SpecialModelVertexAttribute.required("UV2", SpecialModelValueType.UV2),
                SpecialModelVertexAttribute.required("Normal", SpecialModelValueType.NORMAL)));
    }

    public static SpecialModelVertexContract custom(VertexFormat vertexFormat) {
        return custom(CUSTOM_VERTEX_FORMAT, vertexFormat);
    }

    public static SpecialModelVertexContract custom(ResourceLocation vertexFormatId, VertexFormat vertexFormat) {
        return new SpecialModelVertexContract(vertexFormatId, vertexFormat, true, List.of());
    }

    public static SpecialModelVertexContract infer(VertexFormat vertexFormat) {
        if (vertexFormat == DefaultVertexFormat.BLOCK) {
            return block();
        }

        if (vertexFormat == DefaultVertexFormat.NEW_ENTITY) {
            return newEntity();
        }

        return custom(vertexFormat);
    }

    public SpecialModelVertexContract withChunkOffset(boolean requiresChunkOffset) {
        return new SpecialModelVertexContract(this.vertexFormatId, this.vertexFormat, requiresChunkOffset, this.attributes);
    }

    public SpecialModelVertexContract withAttribute(SpecialModelVertexAttribute attribute) {
        List<SpecialModelVertexAttribute> updated = new ArrayList<>(this.attributes);

        for (int i = 0; i < updated.size(); i++) {
            if (updated.get(i).name().equals(attribute.name())) {
                updated.set(i, attribute);
                return new SpecialModelVertexContract(this.vertexFormatId, this.vertexFormat, this.requiresChunkOffset, updated);
            }
        }

        updated.add(attribute);
        return new SpecialModelVertexContract(this.vertexFormatId, this.vertexFormat, this.requiresChunkOffset, updated);
    }

    public SpecialModelVertexContract requireAttribute(String name, SpecialModelValueType type) {
        return this.withAttribute(SpecialModelVertexAttribute.required(name, type));
    }

    public SpecialModelVertexContract optionalAttribute(String name, SpecialModelValueType type) {
        return this.withAttribute(SpecialModelVertexAttribute.optional(name, type));
    }

    public SpecialModelVertexContract requiresUv1() {
        return this.requireAttribute("UV1", SpecialModelValueType.UV1);
    }
}
