package org.dimdev.limlib.client.specialmodels.iris;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public final class SpecialModelShaderpackDefinitionParser {

    public static SpecialModelShaderpackDefinition parse(Reader reader) {
        return parse(JsonParser.parseReader(reader));
    }

    public static SpecialModelShaderpackDefinition parse(JsonElement root) {
        if (!root.isJsonObject()) {
            throw new JsonParseException("LimLib special model metadata root must be an object");
        }

        return parse(root.getAsJsonObject());
    }

    public static SpecialModelShaderpackDefinition parse(JsonObject root) {
        ResourceLocation effectId = ResourceLocation.parse(getString(root, "effect"));
        SpecialModelIrisStage stage = parseStage(getString(root, "stage", SpecialModelIrisStage.TERRAIN_TRANSLUCENT.serializedName()));
        ResourceLocation vertexFormatId = ResourceLocation.parse(getString(root, "vertex_format", "minecraft:block"));
        SpecialModelOutputContract outputContract = parseOutputContract(getObject(root, "outputs", new JsonObject()), root);

        return SpecialModelShaderpackDefinition
            .builder(effectId)
            .metadataVersion(getInt(root, "limlib_special_model", SpecialModelShaderpackDefinition.CURRENT_METADATA_VERSION))
            .stage(stage)
            .vertexFormatId(vertexFormatId)
            .outputContract(outputContract)
            .fallback(parseFallback(getString(root, "fallback", SpecialModelIrisFallback.VANILLA_SPECIAL_SHADER.serializedName())))
            .uniforms(parseUniforms(getArray(root, "uniforms", new JsonArray())))
            .samplers(parseSamplers(getArray(root, "samplers", new JsonArray())))
            .build();
    }

    private static SpecialModelOutputContract parseOutputContract(JsonObject outputs, JsonObject root) {
        int fragmentOutputCount = getInt(outputs, "fragment_output_count", 1);
        int[] drawBuffers = getIntArray(outputs, "draw_buffers", defaultDrawBuffers(fragmentOutputCount));

        return SpecialModelOutputContract
            .builder()
            .fragmentOutputCount(fragmentOutputCount)
            .defaultDrawBuffers(drawBuffers)
            .writesDepth(getBoolean(outputs, "writes_depth", false))
            .requiresPreTranslucentDepth(getBoolean(outputs, "requires_pre_translucent_depth", false))
            .requiresMainDepth(getBoolean(outputs, "requires_main_depth", false))
            .blendMode(parseBlendMode(root))
            .build();
    }

    private static SpecialModelBlendMode parseBlendMode(JsonObject root) {
        if (!has(root, "blend")) {
            return SpecialModelBlendMode.NONE;
        }

        JsonElement blendElement = root.get("blend");

        if (blendElement.isJsonPrimitive()) {
            return parseBlendMode(blendElement.getAsString());
        }

        if (blendElement.isJsonObject()) {
            return parseBlendMode(getString(blendElement.getAsJsonObject(), "mode", SpecialModelBlendMode.NONE.serializedName()));
        }

        throw new JsonParseException("blend must be a string or object");
    }

    private static List<SpecialModelUniformSpec> parseUniforms(JsonArray uniforms) {
        List<SpecialModelUniformSpec> parsed = new ArrayList<>(uniforms.size());

        for (JsonElement uniformElement : uniforms) {
            if (!uniformElement.isJsonObject()) {
                throw new JsonParseException("uniform entries must be objects");
            }

            JsonObject uniform = uniformElement.getAsJsonObject();
            parsed.add(new SpecialModelUniformSpec(
                getString(uniform, "name"),
                parseValueType(getString(uniform, "type")),
                getBoolean(uniform, "required", true),
                parseUniformSource(getString(uniform, "source"))));
        }

        return List.copyOf(parsed);
    }

    private static List<SpecialModelSamplerSpec> parseSamplers(JsonArray samplers) {
        List<SpecialModelSamplerSpec> parsed = new ArrayList<>(samplers.size());

        for (JsonElement samplerElement : samplers) {
            if (!samplerElement.isJsonObject()) {
                throw new JsonParseException("sampler entries must be objects");
            }

            JsonObject sampler = samplerElement.getAsJsonObject();
            parsed.add(new SpecialModelSamplerSpec(
                getString(sampler, "name"),
                getBoolean(sampler, "required", true),
                parseSamplerSource(getString(sampler, "source"))));
        }

        return List.copyOf(parsed);
    }

    private static SpecialModelIrisStage parseStage(String value) {
        return SpecialModelIrisStage
            .bySerializedName(value)
            .orElseThrow(() -> new JsonParseException("unknown LimLib special model stage: " + value));
    }

    private static SpecialModelIrisFallback parseFallback(String value) {
        return SpecialModelIrisFallback
            .bySerializedName(value)
            .orElseThrow(() -> new JsonParseException("unknown LimLib special model fallback: " + value));
    }

    private static SpecialModelBlendMode parseBlendMode(String value) {
        return SpecialModelBlendMode
            .bySerializedName(value)
            .orElseThrow(() -> new JsonParseException("unknown LimLib special model blend mode: " + value));
    }

    private static SpecialModelValueType parseValueType(String value) {
        return SpecialModelValueType
            .bySerializedName(value)
            .orElseThrow(() -> new JsonParseException("unknown LimLib special model value type: " + value));
    }

    private static SpecialModelUniformSource parseUniformSource(String value) {
        return SpecialModelUniformSource
            .bySerializedName(value)
            .orElseThrow(() -> new JsonParseException("unknown LimLib special model uniform source: " + value));
    }

    private static SpecialModelSamplerSource parseSamplerSource(String value) {
        return SpecialModelSamplerSource
            .bySerializedName(value)
            .orElseThrow(() -> new JsonParseException("unknown LimLib special model sampler source: " + value));
    }

    private static boolean has(JsonObject object, String key) {
        return object.has(key) && !object.get(key).isJsonNull();
    }

    private static String getString(JsonObject object, String key) {
        if (!has(object, key)) {
            throw new JsonParseException("missing required string: " + key);
        }

        JsonElement element = object.get(key);

        if (!element.isJsonPrimitive()) {
            throw new JsonParseException(key + " must be a string");
        }

        return element.getAsString();
    }

    private static String getString(JsonObject object, String key, String defaultValue) {
        return has(object, key) ? getString(object, key) : defaultValue;
    }

    private static int getInt(JsonObject object, String key, int defaultValue) {
        return has(object, key) ? object.get(key).getAsInt() : defaultValue;
    }

    private static boolean getBoolean(JsonObject object, String key, boolean defaultValue) {
        return has(object, key) ? object.get(key).getAsBoolean() : defaultValue;
    }

    private static JsonObject getObject(JsonObject object, String key, JsonObject defaultValue) {
        if (!has(object, key)) {
            return defaultValue;
        }

        JsonElement element = object.get(key);

        if (!element.isJsonObject()) {
            throw new JsonParseException(key + " must be an object");
        }

        return element.getAsJsonObject();
    }

    private static JsonArray getArray(JsonObject object, String key, JsonArray defaultValue) {
        if (!has(object, key)) {
            return defaultValue;
        }

        JsonElement element = object.get(key);

        if (!element.isJsonArray()) {
            throw new JsonParseException(key + " must be an array");
        }

        return element.getAsJsonArray();
    }

    private static int[] getIntArray(JsonObject object, String key, int[] defaultValue) {
        if (!has(object, key)) {
            return defaultValue;
        }

        JsonElement element = object.get(key);

        if (!element.isJsonArray()) {
            throw new JsonParseException(key + " must be an array");
        }

        JsonArray array = element.getAsJsonArray();
        int[] values = new int[array.size()];

        for (int i = 0; i < array.size(); i++) {
            values[i] = array.get(i).getAsInt();
        }

        return values;
    }

    private static int[] defaultDrawBuffers(int fragmentOutputCount) {
        int[] drawBuffers = new int[fragmentOutputCount];

        for (int i = 0; i < fragmentOutputCount; i++) {
            drawBuffers[i] = i;
        }

        return drawBuffers;
    }

    private SpecialModelShaderpackDefinitionParser() {
    }
}
