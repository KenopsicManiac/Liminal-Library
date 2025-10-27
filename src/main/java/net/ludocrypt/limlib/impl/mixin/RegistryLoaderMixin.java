package net.ludocrypt.limlib.impl.mixin;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import net.minecraft.core.*;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;

import net.ludocrypt.limlib.api.LimlibRegistryHooks;
import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.effects.post.PostEffect;
import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.api.skybox.Skybox;
import net.ludocrypt.limlib.impl.SaveStorageSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Mixin(RegistryDataLoader.class)
public class RegistryLoaderMixin {
	@Shadow @Final @Mutable
	public static List<RegistryDataLoader.RegistryData<?>> SYNCHRONIZED_REGISTRIES;

	@Shadow @Final @Mutable
	public static List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void limlib$augmentRegistryLists(CallbackInfo ci) {
		// WORLDGEN_REGISTRIES (datapack-driven loading)
		List<RegistryDataLoader.RegistryData<?>> worldgen = new java.util.ArrayList<>(WORLDGEN_REGISTRIES);
		worldgen.add(new RegistryDataLoader.RegistryData<>(PostEffect.POST_EFFECT_KEY, PostEffect.CODEC));
		worldgen.add(new RegistryDataLoader.RegistryData<>(DimensionEffects.DIMENSION_EFFECTS_KEY, DimensionEffects.CODEC));
		worldgen.add(new RegistryDataLoader.RegistryData<>(SoundEffects.SOUND_EFFECTS_KEY, SoundEffects.CODEC));
		worldgen.add(new RegistryDataLoader.RegistryData<>(Skybox.SKYBOX_KEY, Skybox.CODEC));
		WORLDGEN_REGISTRIES = worldgen;

		// SYNCHRONIZED_REGISTRIES (client sync)
		List<RegistryDataLoader.RegistryData<?>> sync = new java.util.ArrayList<>(SYNCHRONIZED_REGISTRIES);
		sync.add(new RegistryDataLoader.RegistryData<>(PostEffect.POST_EFFECT_KEY, PostEffect.CODEC));
		sync.add(new RegistryDataLoader.RegistryData<>(DimensionEffects.DIMENSION_EFFECTS_KEY, DimensionEffects.CODEC));
		sync.add(new RegistryDataLoader.RegistryData<>(SoundEffects.SOUND_EFFECTS_KEY, SoundEffects.CODEC));
		sync.add(new RegistryDataLoader.RegistryData<>(Skybox.SKYBOX_KEY, Skybox.CODEC));
		SYNCHRONIZED_REGISTRIES = sync;
	}

	@Inject(
		method = "loadElementFromResource",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/Decoder;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;",
			shift = At.Shift.BEFORE
		),
		locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private static <E> void limlib$loadRegistryContents(
		WritableRegistry<E> writableRegistry,
		Decoder<E> decoder,
		RegistryOps<JsonElement> registryOps,
		ResourceKey<E> elementKey,
		Resource resource,
		RegistrationInfo registrationInfo,
		CallbackInfo ci,
		Reader reader,
		JsonElement jsonElement
	) {
		if (jsonElement == null) return; // LVT not available; abort safely

		// WORLD_PRESET detection should use the element key (Step 7)
		if (elementKey.isFor(Registries.WORLD_PRESET)) {
			if (jsonElement.isJsonObject()) {
				JsonObject preset = jsonElement.getAsJsonObject();
				JsonObject dimensions = preset.getAsJsonObject("dimensions");
				if (dimensions != null) {
					LimlibWorld.LIMLIB_WORLD.entrySet().forEach(world -> {
						JsonElement stemJson = LevelStem.CODEC
							.encodeStart(registryOps, world.getValue()
								.getDimensionOptionsSupplier()
								.apply(new LimlibWorld.RegistryProvider() {
									@Override
									public <T> HolderGetter<T> get(ResourceKey<Registry<T>> key) {
										return registryOps.getter(key).orElseThrow();
									}
								}))
							.result()
							.orElse(null);
						if (stemJson != null) {
							dimensions.add(world.getKey().location().toString(), stemJson);
						}
					});
				}
			}
		}

		LimlibRegistryHooks.REGISTRY_JSON_HOOKS
			.getOrDefault(writableRegistry.key(), com.google.common.collect.Sets.newHashSet())
			.forEach(h -> ((LimlibRegistryHooks.LimlibJsonRegistryHook<E>) h)
				.register(writableRegistry.key(), registryOps, jsonElement));
	}

	@Inject(
		method = "loadContentsFromManager",
		at = @At("TAIL")
	)
	private static <E> void limlib$afterLoadFromManager(
		ResourceManager resourceManager,
		RegistryOps.RegistryInfoLookup infoLookup,
		WritableRegistry<E> registry,
		Decoder<E> decoder,
		Map<ResourceKey<?>, Exception> loadingErrors,
		CallbackInfo ci
	) {
		// Dynamic DimensionType registrations
		if (registry.key().equals(Registries.DIMENSION_TYPE)) {
			RegistrationInfo stableInfo = new RegistrationInfo(java.util.Optional.empty(), Lifecycle.stable());
			LimlibWorld.LIMLIB_WORLD.entrySet().forEach(world -> {
				ResourceKey<DimensionType> key = ResourceKey.create(Registries.DIMENSION_TYPE, world.getKey().location());
                if (((Registry<DimensionType>) registry).getOptional(key).isEmpty()) {
                    ((WritableRegistry<DimensionType>) registry).register(
                        key,
                        world.getValue().getDimensionTypeSupplier().get(),
                        stableInfo
                    );
                }
			});
		}

		// Post-load programmatic hooks
		LimlibRegistryHooks.REGISTRY_HOOKS
			.getOrDefault((ResourceKey) registry.key(), com.google.common.collect.Sets.newHashSet())
			.forEach(h -> ((LimlibRegistryHooks.LimlibRegistryHook<E>) h)
				.register(infoLookup, (ResourceKey) registry.key(), registry));
	}

	@Inject(
		method = "loadContentsFromNetwork",
		at = @At("TAIL")
	)
	private static <E> void limlib$afterLoadFromNetwork(
		Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> packed,
		ResourceProvider resourceProvider,
		RegistryOps.RegistryInfoLookup infoLookup,
		WritableRegistry<E> registry,
		Decoder<E> decoder,
		Map<ResourceKey<?>, Exception> loadingErrors,
		CallbackInfo ci
	) {
		// Dynamic DimensionType registrations (network path too)
		if (registry.key().equals(Registries.DIMENSION_TYPE)) {
			RegistrationInfo stableInfo = new RegistrationInfo(java.util.Optional.empty(), Lifecycle.stable());
			LimlibWorld.LIMLIB_WORLD.entrySet().forEach(world -> {
				ResourceKey<DimensionType> key = ResourceKey.create(Registries.DIMENSION_TYPE, world.getKey().location());
                if (((Registry<DimensionType>) registry).getOptional(key).isEmpty()) {
                    ((WritableRegistry<DimensionType>) registry).register(
                        key,
                        world.getValue().getDimensionTypeSupplier().get(),
                        stableInfo
                    );
                }
			});
		}

		// Post-load programmatic hooks
		LimlibRegistryHooks.REGISTRY_HOOKS
			.getOrDefault((ResourceKey) registry.key(), com.google.common.collect.Sets.newHashSet())
			.forEach(h -> ((LimlibRegistryHooks.LimlibRegistryHook<E>) h)
				.register(infoLookup, (ResourceKey) registry.key(), registry));
	}

	@Inject(
		method = "load(Lnet/minecraft/resources/RegistryDataLoader$LoadingFunction;Lnet/minecraft/core/RegistryAccess;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;",
		at = @At("RETURN")
	)
	private static void limlib$storeFrozenRegistry(
		RegistryDataLoader.LoadingFunction fn,
		RegistryAccess access,
		List<RegistryDataLoader.RegistryData<?>> data,
		CallbackInfoReturnable<RegistryAccess.Frozen> cir
	) {
		SaveStorageSupplier.LOADED_REGISTRY.set(cir.getReturnValue());
	}

}
