package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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

	@Inject(method = "tickNonPaused", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getSoundSourceVolume(Lnet/minecraft/sounds/SoundSource;)F", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	public void limlib$tick(CallbackInfo ci, Iterator<?> iterator, Map.Entry<?, ?> entry,
							ChannelAccess.ChannelHandle sourceManager, SoundInstance soundInstance) {
		sourceManager.execute(source -> ReverbFilter.update(soundInstance, ((SourceAccessor) source).getSource()));
		sourceManager.execute(source -> DistortionFilter.update(soundInstance, ((SourceAccessor) source).getSource()));
	}

	@Inject(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/ChannelAccess$ChannelHandle;execute(Ljava/util/function/Consumer;)V", ordinal = 0, shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	public void limlib$play(SoundInstance soundInstance, CallbackInfo ci, WeighedSoundEvents weightedSoundSet,
							ResourceLocation identifier, Sound sound, float f, float g, SoundSource soundCategory, float h, float i,
							SoundInstance.Attenuation attenuationType, boolean bl, Vec3 vec3d, boolean bl3, boolean bl4,
							CompletableFuture<?> completableFuture, ChannelAccess.ChannelHandle sourceManager) {
		sourceManager.execute(source -> ReverbFilter.update(soundInstance, ((SourceAccessor) source).getSource()));
		sourceManager.execute(source -> DistortionFilter.update(soundInstance, ((SourceAccessor) source).getSource()));
	}

	@Inject(method = "reload()V", at = @At("TAIL"))
	public void limlib$reloadSounds(CallbackInfo ci) {
		ReverbFilter.update();
		DistortionFilter.update();
	}

	@Shadow
	public abstract void stop(SoundInstance sound);

}
