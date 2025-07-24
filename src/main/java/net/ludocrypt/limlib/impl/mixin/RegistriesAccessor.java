package net.ludocrypt.limlib.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

@Mixin(BuiltInRegistries.class)
public interface RegistriesAccessor {

	@Invoker
	static <T> Registry<T> callRegisterSimple(ResourceKey<? extends Registry<T>> registryKey, BuiltInRegistries.RegistryBootstrap<T> bootstrap) {
		throw new UnsupportedOperationException();
	}

}
