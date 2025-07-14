package net.ludocrypt.limlib.impl.debug.mixin;

import net.ludocrypt.limlib.impl.debug.DebugWorld;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.WorldPresetTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(WorldPresetTagsProvider.class)
public abstract class WorldPresetTagProviderMixin extends TagsProvider<WorldPreset> {

	protected WorldPresetTagProviderMixin(PackOutput output, ResourceKey<Registry<WorldPreset>> key,
										  CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, key, lookupProvider);
	}

	@Inject(method = "addTags", at = @At("TAIL"))
	protected void limlib$configure(HolderLookup.Provider lookup, CallbackInfo ci) {

		this.tag(WorldPresetTags.EXTENDED).add(DebugWorld.DEBUG_KEY);
	}

}
