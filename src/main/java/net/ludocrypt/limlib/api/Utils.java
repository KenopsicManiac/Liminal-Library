package net.ludocrypt.limlib.api;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;

public class Utils {
	public static <T> MappedRegistry<T> createRegistry(ResourceKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).attribute(RegistryAttribute.MODDED).buildAndRegister();
	}

	public static <T> App<RecordCodecBuilder.Mu<T>, Float> floatRangeCodec(String name, float minVal, float maxVal, float defaulVal, Function<T, Float> function) {
		return Codec
			.floatRange(minVal, maxVal)
			.optionalFieldOf(name, defaulVal)
			.stable().forGetter(function);
	}

	public static <T> T register(Registry<T> registry, String name, T value) {
		return Registry.register(registry, Limlib.id(name), value);
	}
}
