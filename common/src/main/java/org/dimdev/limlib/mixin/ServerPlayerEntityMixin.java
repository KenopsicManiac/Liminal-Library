package org.dimdev.limlib.mixin;

import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import org.dimdev.limlib.api.LimlibTravelling;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {

	public ServerPlayerEntityMixin(Level world, BlockPos pos, float f, GameProfile gameProfile) {
		super(world, pos, f, gameProfile);
	}

	@Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/portal/DimensionTransition$PostDimensionTransition;onTransition(Lnet/minecraft/world/entity/Entity;)V", shift = Shift.AFTER))
	public void limlib$moveToWorld(DimensionTransition dimensionTransition, CallbackInfoReturnable<Entity> cir) {

		if (LimlibTravelling.travelingSound != null) {
			this
				.playNotifySound(LimlibTravelling.travelingSound, SoundSource.AMBIENT, LimlibTravelling.travelingVolume,
					LimlibTravelling.travelingPitch);
		}

	}

//	@ModifyArg(method = "changeDimension", at = @At(value = "INVOKE", target = "net/minecraft/network/protocol/game/ClientboundLevelEventPacket.<init> (ILnet/minecraft/core/BlockPos;IZ)V", ordinal = 0), index = 0)
//	private int limlib$moveToWorld(int in) {
//		return 29848748;
//	}

}
