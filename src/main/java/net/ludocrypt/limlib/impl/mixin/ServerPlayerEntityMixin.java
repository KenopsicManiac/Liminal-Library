package net.ludocrypt.limlib.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.ludocrypt.limlib.api.LimlibTravelling;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {

	public ServerPlayerEntityMixin(Level world, BlockPos pos, float f, GameProfile gameProfile) {
		super(world, pos, f, gameProfile);
	}

	@Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "net/minecraft/server/network/ServerGamePacketListenerImpl.send (Lnet/minecraft/network/protocol/Packet;)V", ordinal = 5, shift = Shift.AFTER))
	public void limlib$moveToWorld(ServerLevel to, CallbackInfoReturnable<Entity> ci) {

		if (LimlibTravelling.travelingSound != null) {
			this
				.playNotifySound(LimlibTravelling.travelingSound, SoundSource.AMBIENT, LimlibTravelling.travelingVolume,
					LimlibTravelling.travelingPitch);
		}

	}

	@ModifyArg(method = "changeDimension", at = @At(value = "INVOKE", target = "net/minecraft/network/protocol/game/ClientboundLevelEventPacket.<init> (ILnet/minecraft/core/BlockPos;IZ)V", ordinal = 0), index = 0)
	private int limlib$moveToWorld(int in) {
		return 29848748;
	}

}
