package net.ludocrypt.limlib.impl.data.collectors;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public record CollectorType<T extends NbtGroupCollector>(MapCodec<T> collectorCodec) {
	public static final Registry<CollectorType<?>> REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey(Limlib.id("nbt_collector")), Lifecycle.stable());
}
