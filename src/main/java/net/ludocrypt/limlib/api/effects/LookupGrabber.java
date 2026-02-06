package net.ludocrypt.limlib.api.effects;

import java.util.Optional;

import net.ludocrypt.limlib.api.LimLibRegistries;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class LookupGrabber {

	public static <T> Optional<T> snatch(HolderLookup<T> lookup, ResourceKey<T> key) {
		Optional<Holder.Reference<T>> holderOptional = lookup.get(key);

		if (holderOptional.isPresent()) {
			Holder.Reference<T> holder = holderOptional.get();
			try {
				T held = holder.value();
				return Optional.of(held);
			} catch (IllegalStateException e) {
				return Optional.empty();
			}
		}

		return Optional.empty();
	}

	public static <T> Optional<T> snatch(HolderLookup.RegistryLookup<T> lookup, ResourceLocation location) {
		return snatch(lookup, ResourceKey.create((ResourceKey<? extends Registry<T>>) lookup.key(), location));
	}

	public static <T> Optional<T> snatchFromLevel(Level level, ResourceKey<Registry<T>> key) {
		return level.registryAccess().lookup(key).flatMap(a -> LookupGrabber.snatch(a, level.dimension().location()));
	}
}
