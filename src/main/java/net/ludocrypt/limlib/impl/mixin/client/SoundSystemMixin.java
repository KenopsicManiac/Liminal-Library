package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.Multimap;

import net.ludocrypt.limlib.api.effects.sound.distortion.DistortionFilter;
import net.ludocrypt.limlib.api.effects.sound.reverb.ReverbFilter;
import net.ludocrypt.limlib.impl.access.SoundSystemAccess;

@Mixin(SoundEngine.class)
public abstract class SoundSystemMixin implements SoundSystemAccess {


	@Shadow
	@Final
	private Multimap<SoundSource, SoundInstance> instanceBySource;

	@Override
	public void stopSoundsAtPosition(double x, double y, double z, @Nullable ResourceLocation id,
									 @Nullable SoundSource category) {
		Consumer<SoundInstance> consumer = (soundInstance) -> {

			if ((id != null ? soundInstance.getLocation().equals(id)
				: true) && (soundInstance.getX() == x) && (soundInstance.getY() == y) && (soundInstance.getZ() == z)) {
				this.stop(soundInstance);
			}

		};

		if (category != null) {
			this.instanceBySource.get(category).forEach(consumer);
		} else {
			this.instanceBySource.forEach((soundCategory, soundInstance) -> consumer.accept(soundInstance));
		}

	}

	@WrapOperation(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/ChannelAccess$ChannelHandle;execute(Ljava/util/function/Consumer;)V"))
	private static void play(ChannelAccess.ChannelHandle instance, Consumer<Channel> action, Operation<Void> original, @Local(argsOnly = true) SoundInstance soundInstance) {
		instance.execute(channel -> {
			var id = ((SourceAccessor) channel).getSource();
			ReverbFilter.update(soundInstance, id);
			DistortionFilter.update(soundInstance, id);
		});

		original.call(instance, action);
	}

	@Inject(method = "reload()V", at = @At("TAIL"))
	public void limlib$reloadSounds(CallbackInfo ci) {
		ReverbFilter.update();
		DistortionFilter.update();
	}

	@Shadow
	public abstract void stop(SoundInstance sound);

}
