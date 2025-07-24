package net.ludocrypt.limlib.impl.mixin;

import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldPreset.class)
public interface GeneratorTypeAccessor {

	@Accessor
	Map<ResourceKey<LevelStem>, LevelStem> getDimensions();

	@Mutable
	@Accessor
	void setDimensions(Map<ResourceKey<LevelStem>, LevelStem> dimensions);

}
