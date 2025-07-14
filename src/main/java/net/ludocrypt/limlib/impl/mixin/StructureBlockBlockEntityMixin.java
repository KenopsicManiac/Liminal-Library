package net.ludocrypt.limlib.impl.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.ludocrypt.limlib.impl.access.StructureBlockBlockEntityAccess;

@Mixin(StructureBlockEntity.class)
public class StructureBlockBlockEntityMixin implements StructureBlockBlockEntityAccess {

	@Unique
	CompoundTag tags;

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	protected void limlib$writeNbt(CompoundTag nbt, CallbackInfo ci) {

		if (tags != null) {
			nbt.put("limlib_tag", tags);
		}

	}

	@Inject(method = "load", at = @At("TAIL"))
	protected void limlib$readNbt(CompoundTag nbt, CallbackInfo ci) {

		if (nbt.contains("limlib_tag")) {
			this.tags = nbt.getCompound("limlib_tag");
		}

	}

	@Inject(method = "saveStructure(Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;setAuthor(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	protected void limlib$saveStructure(boolean bl, CallbackInfoReturnable<Boolean> ci, BlockPos blockPos,
										ServerLevel serverWorld, StructureTemplateManager structureTemplateManager, StructureTemplate structure) {

		if (tags != null) {
			((StructureBlockBlockEntityAccess) structure).setTags(tags);
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
