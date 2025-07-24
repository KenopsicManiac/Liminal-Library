//package net.ludocrypt.limlib.impl.mixin;
//
//import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
//import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.At.Shift;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.ImmutableMap.Builder;
//import com.mojang.serialization.Codec;
//
//import net.ludocrypt.limlib.api.effects.post.PostEffect;
//import net.ludocrypt.limlib.api.effects.sky.DimensionEffects;
//import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
//import net.ludocrypt.limlib.api.skybox.Skybox;
//import net.minecraft.core.Registry;
//import net.minecraft.core.RegistrySynchronization;
//import net.minecraft.resources.ResourceKey;
//
//@Mixin(RegistrySynchronization.class)
//public abstract class DynamicRegistrySyncMixin { TODO: Figure how the frack to do this and if needed.
//
//	@Shadow
//	protected static <E> void put(Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.PackedRegistryEntry> builder, ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec) {
//		DynamicRegistries.register();
//	}
//
//	@Inject(method = "method_45958()Lcom/google/common/collect/ImmutableMap;", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/RegistrySynchronization;put(Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/resources/ResourceKey;Lcom/mojang/serialization/Codec;)V", ordinal = 2, shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
//	private static void limlib$makeMap$mapped(
//			CallbackInfoReturnable<ImmutableMap<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>>> ci,
//			Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder) {
//		put(builder, PostEffect.POST_EFFECT_KEY, PostEffect.CODEC);
//		put(builder, DimensionEffects.DIMENSION_EFFECTS_KEY, DimensionEffects.CODEC);
//		put(builder, SoundEffects.SOUND_EFFECTS_KEY, SoundEffects.CODEC);
//		put(builder, Skybox.SKYBOX_KEY, Skybox.CODEC);
//	}
//}
