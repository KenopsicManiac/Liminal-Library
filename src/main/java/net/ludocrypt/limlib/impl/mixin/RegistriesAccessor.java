package net.ludocrypt.limlib.impl.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.serialization.Lifecycle;

@Mixin(BuiltInRegistries.class)
public interface RegistriesAccessor {

	@Invoker
	static <T> Registry<T> callRegisterSimple(ResourceKey<? extends Registry<T>> registryKey, Lifecycle lifecycle,
											  BuiltInRegistries.RegistryBootstrap<T> bootstrap) {
		throw new UnsupportedOperationException();
	}

}
