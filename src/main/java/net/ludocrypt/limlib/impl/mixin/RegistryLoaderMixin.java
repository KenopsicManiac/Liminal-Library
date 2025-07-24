package net.ludocrypt.limlib.impl.mixin;

import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;

import net.ludocrypt.limlib.api.LimlibRegistryHooks;
import net.ludocrypt.limlib.api.LimlibRegistryHooks.LimlibJsonRegistryHook;
import net.ludocrypt.limlib.api.LimlibRegistryHooks.LimlibRegistryHook;
import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.LimlibWorld.RegistryProvider;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.impl.SaveStorageSupplier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Mixin(RegistryDataLoader.class)
public class RegistryLoaderMixin {

	@Shadow
	@Final
	@Mutable
	public static List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES;
	static {
		List<RegistryDataLoader.RegistryData<?>> newRegistries = Lists.newArrayList();
		newRegistries.addAll(WORLDGEN_REGISTRIES);
		newRegistries.add(new RegistryDataLoader.RegistryData(PostEffect.POST_EFFECT_KEY, PostEffect.CODEC));
		newRegistries.add(new RegistryDataLoader.RegistryData(DimensionEffects.DIMENSION_EFFECTS_KEY, DimensionEffects.CODEC));
		newRegistries.add(new RegistryDataLoader.RegistryData(SoundEffects.SOUND_EFFECTS_KEY, SoundEffects.CODEC));
		newRegistries.add(new RegistryDataLoader.RegistryData(Skybox.SKYBOX_KEY, Skybox.CODEC));
		WORLDGEN_REGISTRIES = newRegistries;
	}

	@Inject(method = "loadRegistryContents", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Decoder;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private static <E> void limlib$loadRegistryContents(RegistryOps.RegistryInfoLookup infoLookup,
			ResourceManager resourceManager, ResourceKey<? extends Registry<E>> registryKey, WritableRegistry<E> registry,
			Decoder<E> decoder, Map<ResourceKey<?>, Exception> readFailures, CallbackInfo ci, String string,
			FileToIdConverter resourceFileNamespace, RegistryOps<JsonElement> registryOps,
			Iterator<Map.Entry<ResourceLocation, Resource>> var9, Map.Entry<ResourceLocation, Resource> entry, ResourceLocation identifier,
			ResourceKey<E> registryKey2, Resource resource, Reader reader, JsonElement jsonElement) {

		if (registryKey2.isFor(Registries.WORLD_PRESET)) {
			JsonObject presetType = jsonElement.getAsJsonObject();
			JsonObject dimensions = presetType.get("dimensions").getAsJsonObject();
			LimlibWorld.LIMLIB_WORLD
				.entrySet()
				.forEach((world) -> dimensions
					.add(world.getKey().location().toString(),
						LevelStem.CODEC
							.encodeStart(registryOps,
								world.getValue().getDimensionOptionsSupplier().apply(new RegistryProvider() {

									@Override
									public <T> HolderGetter<T> get(ResourceKey<Registry<T>> key) {
										return registryOps.getter(key).get();
									}

								}))
							.result()
							.get()));
		}

		LimlibRegistryHooks.REGISTRY_JSON_HOOKS
			.getOrDefault(registryKey, Sets.newHashSet())
			.forEach((registrarhook -> ((LimlibJsonRegistryHook<E>) registrarhook)
				.register(infoLookup, registryKey, registryOps, jsonElement)));
	}

	@Inject(method = "loadRegistryContents", at = @At("TAIL"))
	private static <E> void limlib$loadRegistryContents(RegistryOps.RegistryInfoLookup infoLookup,
			ResourceManager resourceManager, ResourceKey<? extends Registry<E>> registryKey, WritableRegistry<E> registry,
			Decoder<E> decoder, Map<ResourceKey<?>, Exception> readFailures, CallbackInfo ci) {

		if (registryKey.equals(Registries.DIMENSION_TYPE)) {
			LimlibWorld.LIMLIB_WORLD
				.entrySet()
				.forEach((world) -> ((WritableRegistry<DimensionType>) registry)
					.register(ResourceKey.create(Registries.DIMENSION_TYPE, world.getKey().location()),
						world.getValue().getDimensionTypeSupplier().get(), Lifecycle.stable()));
		}

		LimlibRegistryHooks.REGISTRY_HOOKS
			.getOrDefault(registryKey, Sets.newHashSet())
			.forEach((registrarhook -> ((LimlibRegistryHook<E>) registrarhook).register(infoLookup, registryKey, registry)));
	}

	@Inject(method = "load", at = @At("TAIL"))
	private static void limlib$loadRegistriesIntoManager(ResourceManager resourceManager,
			RegistryAccess registryManager, List<RegistryDataLoader.RegistryData<?>> decodingData,
			CallbackInfoReturnable<RegistryAccess.Frozen> ci) {
		SaveStorageSupplier.LOADED_REGISTRY.set(registryManager.freeze());
	}

}
