package net.ludocrypt.limlib.api.effects;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

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

}
