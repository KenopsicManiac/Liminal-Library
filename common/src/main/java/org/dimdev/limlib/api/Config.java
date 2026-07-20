package org.dimdev.limlib.api;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final String FILE_NAME = "dimdoors-config.json5";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(new TypeToken<ResourceKey<Level>>() {}.getType(), new LevelKeyAdapter())
            .create();

    public static Path getConfigPath(Path configRoot) {
        return configRoot.resolve(FILE_NAME);
    }

    public static <T extends Config> T load(ISided<?> sided, Class<T> configClass) {
        return load(configClass, getConfigPath(sided.getConfigRoot()), sided.getModId());
    }

    public static <T extends Config> T load(ISided<?> sided, Class<T> configClass, Path configRoot) {
        return load(configClass, getConfigPath(configRoot), sided.getModId());
    }

    private static <T extends Config> T load(Class<T> configClass, Path configPath, String modId) {
        if (!Files.exists(configPath)) {
            T config = createInstance(configClass);
            save(config, configPath, modId);
            return config;
        }

        try (var reader = Files.newBufferedReader(configPath)) {
            T config = GSON.fromJson(reader, configClass);
            return config == null ? createInstance(configClass) : config;
        } catch (IOException | JsonParseException e) {
            throw new IllegalStateException("Failed to load " + modId + " config from " + configPath, e);
        }
    }

    public static <T extends Config> T createInstance(Class<T> configClass) {
        try {
            Constructor<T> constructor = configClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to create config instance for " + configClass.getName(), e);
        }
    }

    public void save(ISided<?> sided) {
        save(this, getConfigPath(sided.getConfigRoot()), sided.getModId());
    }

    private static void save(Config config, Path configPath, String modId) {
        try {
            Files.createDirectories(configPath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                writer.write(GSON.toJson(config));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save " + modId + " config to " + configPath, e);
        }
    }

    public static final class LevelKeyAdapter implements JsonSerializer<ResourceKey<Level>>, JsonDeserializer<ResourceKey<Level>> {

        @Override
        public ResourceKey<Level> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(json.getAsJsonPrimitive().getAsString()));
        }

        @Override
        public JsonElement serialize(ResourceKey<Level> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.location().toString());
        }
    }
}
