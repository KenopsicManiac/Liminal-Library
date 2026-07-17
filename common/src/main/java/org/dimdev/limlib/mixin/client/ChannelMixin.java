package org.dimdev.limlib.mixin.client;

import com.mojang.blaze3d.audio.Channel;
import org.dimdev.limlib.api.client.ChannelExt;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Channel.class)
public abstract class ChannelMixin implements ChannelExt {
    @Shadow
    @Final
    private int source;

//	This needed for reverb to work due to details described in this link. https://github.com/kcat/openal-soft/issues/943#issuecomment-1845042792

    @Inject(method = "linearAttenuation", at = @At("RETURN"))
    private void linearAttenuation2(float attenuation, CallbackInfo ci) {
        AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, attenuation / 2F);
    }

	@Override
	public int getSource() {
		return source;
	}
}
