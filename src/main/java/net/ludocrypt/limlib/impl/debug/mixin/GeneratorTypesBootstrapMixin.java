package net.ludocrypt.limlib.impl.debug.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.ludocrypt.limlib.impl.debug.DebugNbtChunkGenerator;
import net.ludocrypt.limlib.impl.debug.DebugWorld;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

@Mixin(targets = "net/minecraft/world/level/levelgen/presets/WorldPresets$Bootstrap")
public abstract class GeneratorTypesBootstrapMixin {

	@Shadow
	private HolderGetter<Biome> biomes;

	@Shadow
	protected abstract void registerCustomOverworldPreset(ResourceKey<WorldPreset> resourceKey, LevelStem levelStem);

	@Shadow
	@Final
	private Holder<DimensionType> overworldDimensionType;

	@Inject(method = "bootstrap", at = @At("TAIL"))
	public void limlib$addDimensionOpions(CallbackInfo ci) {
		this
			.registerCustomOverworldPreset(DebugWorld.DEBUG_KEY, new LevelStem(this.overworldDimensionType,
				new DebugNbtChunkGenerator(this.biomes.getOrThrow(Biomes.THE_VOID))));
	}
}
