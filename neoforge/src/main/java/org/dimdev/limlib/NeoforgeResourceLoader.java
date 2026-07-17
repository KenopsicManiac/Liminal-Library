package org.dimdev.limlib;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NeoforgeResourceLoader {
	public static class Server extends ContextAwareReloadListener implements ResourceManagerReloadListener {
		private final BiConsumer<HolderLookup.Provider, ResourceManager> consumer;

		public Server(BiConsumer<HolderLookup.Provider, ResourceManager> consumer) {
			this.consumer = consumer;
		}

		@Override
		public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
			consumer.accept(this.getRegistryLookup(), resourceManager);
		}
	}

	public static class Client implements ResourceManagerReloadListener {
		private final Consumer<ResourceManager> consumer;

		public Client(Consumer<ResourceManager> consumer) {
			this.consumer = consumer;
		}

		@Override
		public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
			consumer.accept(resourceManager);
		}
	}
}
