package net.ludocrypt.limlib.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.Set;

public class LimlibTravelling {

	@Internal
	public static SoundEvent travelingSound = null;

	@Internal
	public static float travelingVolume = 0.25F;

	@Internal
	public static float travelingPitch = 1.0F;

	public static <E extends Entity> E travelTo(E teleported, ServerLevel destination, DimensionTransition target,
												SoundEvent sound, float volume, float pitch) {

		if (destination.equals(teleported.level())) {

			BlockPos blockPos = BlockPos.containing(target.pos().x, target.pos().y, target.pos().z);

			if (!Level.isInSpawnableBounds(blockPos)) {
				throw new UnsupportedOperationException("Position " + blockPos.toString() + " is out of this world!");
			}

			float f = Mth.wrapDegrees(target.yRot());
			float g = Mth.wrapDegrees(target.xRot());

			if (teleported instanceof ServerPlayer) {
				ChunkPos chunkPos = new ChunkPos(blockPos);
				destination.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, teleported.getId());
				teleported.stopRiding();

				if (((ServerPlayer) teleported).isSleeping()) {
					((ServerPlayer) teleported).stopSleepInBed(true, true);
				}

				((ServerPlayer) teleported).connection
					.teleport(target.pos().x, target.pos().y, target.pos().z, f, g, Set.of());

				teleported.setYHeadRot(f);
			} else {
				float h = Mth.clamp(g, -90.0f, 90.0f);
				teleported.moveTo(target.pos().x, target.pos().y, target.pos().z, f, h);
				teleported.setYHeadRot(f);
			}

			teleported.setDeltaMovement(target.speed());
			teleported
				.level()
				.playSound(null, teleported.getX(), teleported.getY(), teleported.getZ(), sound, SoundSource.AMBIENT,
					volume, pitch);

			return teleported;
		} else {

			try {
				travelingSound = sound;
				travelingVolume = volume;
				travelingPitch = pitch;
				return (E) teleported.changeDimension(target);
			} finally {
				travelingSound = null;
				travelingVolume = 0.0F;
				travelingPitch = 0.0F;
			}

		}

	}

}
