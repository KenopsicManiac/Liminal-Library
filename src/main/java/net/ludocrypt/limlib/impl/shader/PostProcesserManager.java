package net.ludocrypt.limlib.impl.shader;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;

public final class PostProcesserManager implements SimpleSynchronousResourceReloadListener {

	public static final PostProcesserManager INSTANCE = new PostProcesserManager();
	public static final ResourceLocation RESOURCE_KEY = new ResourceLocation("limlib:shaders");

	private final Set<PostProcesser> shaders = new ReferenceOpenHashSet<>();

	public PostProcesser find(ResourceLocation location) {
		PostProcesser ret = new PostProcesser(location);
		shaders.add(ret);
		return ret;
	}

	public void onResolutionChanged(int newWidth, int newHeight) {

		if (!shaders.isEmpty()) {

			for (PostProcesser shader : shaders) {

				if (shader.isInitialized()) {
					Minecraft client = Minecraft.getInstance();
					shader.shader
						.resize(client.getWindow().getWidth(),
							client.getWindow().getHeight());
				}

			}

		}

	}

	@Override
	public ResourceLocation getFabricId() {
		return RESOURCE_KEY;
	}

	@Override
	public void onResourceManagerReload(ResourceManager mgr) {

		for (PostProcesser shader : shaders) {
			shader.init(mgr);
		}

	}

	public void dispose(PostProcesser shader) {
		shader.release();
		shaders.remove(shader);
	}

}
