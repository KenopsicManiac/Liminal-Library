package org.dimdev.limlib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.dimdev.limlib.client.specialmodels.SpecialModelLoadingPlugin;
import org.dimdev.limlib.impl.LimlibClient;
import org.dimdev.limlib.impl.client.LimLibClientSided;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Set;

@Mod(value = "limlib", dist = Dist.CLIENT)
public class LimLibNeofogeClient extends NeoForgeClientSided<LimLibNeofogeClient, LimlibClient> implements LimLibClientSided<LimLibNeofogeClient> {
	private static final Field BAKED_MODEL_WRAPPER_ORIGINAL_MODEL = findOriginalModelField();
	private boolean specialModelLoadingPluginRegistered;

	public LimLibNeofogeClient(IEventBus bus, ModContainer container) {
		super(bus, container, LimlibClient.INSTANCE);
		bus.addListener(this::registerAdditionalSpecialModels);
	}


	@Override
	public void registerSpecialModelLoadingPlugin() {
		this.specialModelLoadingPluginRegistered = true;
	}

	private void registerAdditionalSpecialModels(ModelEvent.RegisterAdditional event) {
		if (!this.specialModelLoadingPluginRegistered) {
			return;
		}

		SpecialModelLoadingPlugin.prepareModelLoading();
		Set<ResourceLocation> specialModelIds = SpecialModelLoadingPlugin.scanSpecialModelIds(Minecraft.getInstance().getResourceManager());

		for (ResourceLocation specialModelId : specialModelIds) {
			event.register(ModelResourceLocation.standalone(specialModelId));
		}
	}

	@Override
	public @Nullable BakedModel getWrappedBakedModel(BakedModel model) {
		if (!(model instanceof BakedModelWrapper<?>)) {
			return null;
		}

		try {
			Object wrappedModel = BAKED_MODEL_WRAPPER_ORIGINAL_MODEL.get(model);
			return wrappedModel instanceof BakedModel bakedModel ? bakedModel : null;
		} catch (IllegalAccessException exception) {
			throw new RuntimeException("Unable to read wrapped NeoForge baked model", exception);
		}
	}

	private static Field findOriginalModelField() {
		try {
			Field field = BakedModelWrapper.class.getDeclaredField("originalModel");
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}
}


