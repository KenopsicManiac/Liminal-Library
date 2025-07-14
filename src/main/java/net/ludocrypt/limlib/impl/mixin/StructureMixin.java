package net.ludocrypt.limlib.impl.mixin;

import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.ludocrypt.limlib.impl.access.StructureBlockBlockEntityAccess;

@Mixin(StructureTemplate.class)
public class StructureMixin implements StructureBlockBlockEntityAccess {

	@Unique
	CompoundTag tags;

	@Inject(method = "save", at = @At("TAIL"))
	protected void limlib$writeNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> ci) {

		if (tags != null) {
			nbt.put("limlib_tag", tags);
		}

	}

	@Inject(method = "load", at = @At("TAIL"))
	protected void limlib$readNbt(HolderGetter<Block> blockProvider, CompoundTag nbt, CallbackInfo ci) {

		if (nbt.contains("limlib_tag")) {
			this.tags = nbt.getCompound("limlib_tag");
		}

	}

	@Override
	public CompoundTag getTags() {
		return tags;
	}

	@Override
	public void setTags(CompoundTag tags) {
		this.tags = tags;
	}

}
