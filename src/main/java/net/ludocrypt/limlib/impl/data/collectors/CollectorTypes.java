package net.ludocrypt.limlib.impl.data.collectors;

import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class CollectorTypes {
	public static final CollectorType<StaticNbtGroupCollector> STATIC_GROUP_COLLECTOR = register(Limlib.id("static_collector"), new CollectorType<>(StaticNbtGroupCollector.CODEC));
	public static final CollectorType<DynamicNbtGroupCollector> DYNAMIC_GROUP_COLLECTOR = register(Limlib.id("dynamic_collector"), new CollectorType<>(DynamicNbtGroupCollector.CODEC));

	public static <T extends NbtGroupCollector> CollectorType<T> register(ResourceLocation id, CollectorType<T> type) {
		return Registry.register(CollectorType.REGISTRY, id, type);
	}

	public static void init() {

	}
}
