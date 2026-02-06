package net.ludocrypt.limlib.impl.mixin.client;

import com.mojang.blaze3d.platform.Window;

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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;

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
			LookupGrabber.snatchFromLevel(level, SoundEffects.SOUND_EFFECTS_KEY).flatMap(SoundEffects::music).ifPresent(ci::setReturnValue);
		}
	}

	@Inject(method = "resizeDisplay", at = @At("RETURN"))
	private void limlib$onResolutionChanged(CallbackInfo info) {
		PostProcesserManager.INSTANCE
			.onResolutionChanged(this.window.getWidth(), this.window.getHeight());
	}

}
