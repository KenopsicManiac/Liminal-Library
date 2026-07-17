package org.dimdev.limlib.mixin.client;

import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.dimdev.limlib.api.client.ChannelExt;
import org.dimdev.limlib.client.effects.sound.distortion.DistortionFilter;
import org.dimdev.limlib.client.effects.sound.reverb.ReverbFilter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Multimap;

import org.dimdev.limlib.impl.access.SoundSystemAccess;

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
			var id = ((ChannelExt) channel).liminal_Library$getSource();
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
