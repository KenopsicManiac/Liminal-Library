package net.ludocrypt.limlib.impl.mixin;

import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;

import net.ludocrypt.limlib.api.LimlibWorld;
import net.ludocrypt.limlib.api.LimlibWorld.RegistryProvider;
import net.ludocrypt.limlib.impl.SaveStorageSupplier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;

@Mixin(LevelStorageSource.class)
public class WorldSaveStorageMixin {

	@ModifyArg(method = "getLevelDataAndDimensions", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/Dynamic;)Lcom/mojang/serialization/DataResult;"))
	private static <T> Dynamic<T> limlib$readGeneratorProperties$datafix(Dynamic<T> value) {
		Dynamic<T> dynamic = value;

		for (Entry<ResourceKey<LimlibWorld>, LimlibWorld> entry : LimlibWorld.LIMLIB_WORLD.entrySet()) {
			dynamic = limlib$addDimension(entry.getKey(), entry.getValue(), dynamic);
		}

		return dynamic;
	}

	@Unique
	@SuppressWarnings("unchecked")
	private static <T> Dynamic<T> limlib$addDimension(ResourceKey<LimlibWorld> key, LimlibWorld world, Dynamic<T> in) {
		Dynamic<T> dimensions = in.get("dimensions").orElseEmptyMap();

		if (dimensions.get(key.location().toString()).result().isEmpty()) {
			Map<Dynamic<T>, Dynamic<T>> dimensionsMap = Maps.newHashMap(dimensions.getMapValues().result().get());

			RegistryAccess registryManager = SaveStorageSupplier.LOADED_REGISTRY.get();

			dimensionsMap
				.put(dimensions.createString(key.location().toString()),
					new Dynamic<T>(dimensions.getOps(),
						(T) LevelStem.CODEC
							.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryManager),
								world.getDimensionOptionsSupplier().apply(new RegistryProvider() {

									@Override
									public <Q> HolderGetter<Q> get(ResourceKey<Registry<Q>> key) {
										return registryManager.lookup(key).get();
									}

								}))
							.result()
							.get()));
			in = in.set("dimensions", in.createMap(dimensionsMap));
		}

		return in;
	}

}
