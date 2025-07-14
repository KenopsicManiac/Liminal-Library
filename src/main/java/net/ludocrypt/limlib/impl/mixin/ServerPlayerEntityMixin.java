package net.ludocrypt.limlib.impl.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.ludocrypt.limlib.api.LimlibTravelling;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {
	@Shadow
	public abstract void playNotifySound(SoundEvent soundEvent, SoundSource soundSource, float f, float g);

	public ServerPlayerEntityMixin(Level world, BlockPos pos, float f, GameProfile gameProfile) {
		super(world, pos, f, gameProfile);
	}

	@Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 5, shift = Shift.AFTER))
	public void limlib$moveToWorld(ServerLevel serverLevel, CallbackInfoReturnable<Entity> cir) {

		if (LimlibTravelling.travelingSound != null) {
			this
				.playNotifySound(LimlibTravelling.travelingSound, SoundSource.AMBIENT, LimlibTravelling.travelingVolume,
					LimlibTravelling.travelingPitch);
		}

	}

	@ModifyArg(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundLevelEventPacket;<init>(ILnet/minecraft/core/BlockPos;IZ)V"), index = 0)
	private int replaceLevelEventId(int original) {
		return 29848748;
	}

}
