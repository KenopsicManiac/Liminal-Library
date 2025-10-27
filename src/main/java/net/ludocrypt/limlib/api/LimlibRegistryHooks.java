package net.ludocrypt.limlib.api;

import java.util.Map;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.RegistryOps.RegistryInfoLookup;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus.Internal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;

public class LimlibRegistryHooks {

	@Internal
	public static final Map<ResourceKey<? extends Registry<?>>, Set<LimlibRegistryHook<?>>> REGISTRY_HOOKS = Maps
		.newHashMap();
	@Internal
	public static final Map<ResourceKey<? extends Registry<?>>, Set<LimlibJsonRegistryHook<?>>> REGISTRY_JSON_HOOKS = Maps
		.newHashMap();

	public static <O, T extends Registry<O>> void hook(ResourceKey<T> key, LimlibRegistryHook<O> hook) {
		Set<LimlibRegistryHook<?>> hooks = REGISTRY_HOOKS.computeIfAbsent(key, k -> Sets.newHashSet());
		hooks.add(hook);
	}

	public static <O, T extends Registry<O>> void hook(ResourceKey<T> key, LimlibJsonRegistryHook<O> hook) {
		Set<LimlibJsonRegistryHook<?>> hooks = REGISTRY_JSON_HOOKS.computeIfAbsent(key, k -> Sets.newHashSet());
		hooks.add(hook);
	}

	@FunctionalInterface
	public interface LimlibRegistryHook<O> {

		/**
		 * @param infoLookup  The full registry lookup.
		 * @param registryKey The RegistryKey of the registry.
		 * @param registry    The MutableRegistry where to register.
		 */
		void register(RegistryInfoLookup infoLookup, ResourceKey<? extends Registry<O>> registryKey,
				WritableRegistry<O> registry);

	}

	@FunctionalInterface
	public interface LimlibJsonRegistryHook<O> {

		/**
		 * @param registry    The MutableRegistry where to register.
		 * @param registryKey The RegistryKey of the registry.
		 * @param registryOps The full registry lookup.
		 * @param jsonElement The jsonElement to modify before being read by a CODEC.
		 */
		void register(ResourceKey<? extends Registry<O>> registryKey,
				RegistryOps<JsonElement> registryOps, JsonElement jsonElement);

	}

}
