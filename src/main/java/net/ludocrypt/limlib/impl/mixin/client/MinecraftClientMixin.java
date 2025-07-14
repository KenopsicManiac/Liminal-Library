package net.ludocrypt.limlib.impl.mixin.client;

import java.util.Optional;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.limlib.api.effects.LookupGrabber;
import net.ludocrypt.limlib.api.effects.sound.SoundEffects;
import net.ludocrypt.limlib.impl.shader.PostProcesserManager;
@Mixin(Minecraft.class)
public class MinecraftClientMixin {

	@Shadow
	public LocalPlayer player;

	@Shadow
	public ClientLevel level;

	@Final
	@Shadow
	private Window window;

	@Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
	private void limlib$getMusic(CallbackInfoReturnable<Music> ci) {

		if (this.player != null) {
			Optional<SoundEffects> soundEffects = LookupGrabber
				.snatch(level.registryAccess().lookup(SoundEffects.SOUND_EFFECTS_KEY).get(),
					ResourceKey.create(SoundEffects.SOUND_EFFECTS_KEY, level.dimension().location()));

			if (soundEffects.isPresent()) {
				Optional<Music> musicSound = soundEffects.get().getMusic();

				musicSound.ifPresent(ci::setReturnValue);

			}

		}

	}

	@Inject(method = "resizeDisplay", at = @At("RETURN"))
	private void limlib$onResolutionChanged(CallbackInfo info) {
		PostProcesserManager.INSTANCE
			.onResolutionChanged(this.window.getWidth(), this.window.getHeight());
	}

}
