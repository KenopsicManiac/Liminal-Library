package org.dimdev.limlib.client.specialmodels.mixin.sable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.vanilla.SingleBlockSubLevelWrapper;
import dev.ryanhcode.sable.sublevel.render.vanilla.VanillaSingleSubLevelRenderData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.limlib.client.specialmodels.SpecialModelLoadingPlugin;
import org.dimdev.limlib.client.specialmodels.SpecialModelRenderTypes;
import org.dimdev.limlib.client.specialmodels.SpecialModelShaderRegistry;
import org.joml.Matrix4f;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = VanillaSingleSubLevelRenderData.class, remap = false)
public abstract class VanillaSingleSubLevelRenderDataMixin {
    @Unique
    private static final RandomSource LIMLIB_RANDOM = RandomSource.create();
    @Unique
    private static final SingleBlockSubLevelWrapper LIMLIB_LEVEL_WRAPPER = new SingleBlockSubLevelWrapper();
    @Unique
    private static final Matrix4f LIMLIB_TRANSFORM = new Matrix4f();
    @Unique
    private static final Vector3d LIMLIB_CENTER_OF_ROTATION = new Vector3d();

    @Shadow
    @Final
    private ClientSubLevel subLevel;

    @Shadow
    private BlockState singleBlockState;

    @Shadow
    private BlockPos singleBlockPos;

    @Shadow
    private long singleBlockSeed;

    @Shadow
    public abstract void rebuild();

    @Inject(method = "renderSingleBlock", at = @At("HEAD"), cancellable = true)
    private void limlib$renderSingleBlockSpecialModelLayer(RenderType layer, VertexConsumer consumer, Matrix4f modelView, double camX, double camY, double camZ, CallbackInfo ci) {
        if (!SpecialModelRenderTypes.isSpecialModelRenderType(layer)) {
            return;
        }

        ci.cancel();

        if (this.singleBlockState.isAir()) {
            this.rebuild();
        }

        if (this.singleBlockState.getRenderShape() != RenderShape.MODEL) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = client.getBlockRenderer();
        BakedModel bakedModel = blockRenderer.getBlockModel(this.singleBlockState);
        List<SpecialModelLoadingPlugin.SpecialModelPart> specialModelParts =
            SpecialModelLoadingPlugin.getSpecialModelParts(bakedModel, this.singleBlockState, this.singleBlockSeed);

        if (specialModelParts.isEmpty()) {
            return;
        }

        Pose3dc renderPose = this.subLevel.renderPose();
        Vector3dc renderPos = renderPose.position();
        LIMLIB_LEVEL_WRAPPER.setup(this.subLevel.getLevel(), renderPos.x(), renderPos.y(), renderPos.z(), this.singleBlockPos, this.singleBlockState);

        try {
            PoseStack poseStack = new PoseStack();
            limlib$setupSingleBlockPose(poseStack, renderPose, renderPos, modelView, camX, camY, camZ);

            int light = LevelRenderer.getLightColor(LIMLIB_LEVEL_WRAPPER, this.singleBlockState, this.singleBlockPos);
            for (SpecialModelLoadingPlugin.SpecialModelPart specialModelPart : specialModelParts) {
                if (specialModelPart.renderType() != layer) {
                    continue;
                }

                LIMLIB_RANDOM.setSeed(this.singleBlockSeed);
                int overlay = SpecialModelShaderRegistry.appendOverlayState(
                    specialModelPart.rendererId(),
                    LIMLIB_LEVEL_WRAPPER,
                    this.singleBlockPos,
                    this.singleBlockState,
                    specialModelPart.model(),
                    this.singleBlockSeed);
                blockRenderer.getModelRenderer().renderModel(
                    poseStack.last(),
                    consumer,
                    this.singleBlockState,
                    specialModelPart.model(),
                    1.0F,
                    1.0F,
                    1.0F,
                    light,
                    overlay);
            }
        } finally {
            LIMLIB_LEVEL_WRAPPER.clear();
        }
    }

    @Unique
    private void limlib$setupSingleBlockPose(PoseStack poseStack, Pose3dc renderPose, Vector3dc renderPos, Matrix4f modelView,
                                             double camX, double camY, double camZ) {
        double renderX = renderPos.x();
        double renderY = renderPos.y();
        double renderZ = renderPos.z();
        Quaterniondc renderRot = renderPose.orientation();
        Vector3d renderCenter = renderRot.transform(
            LIMLIB_CENTER_OF_ROTATION
                .set(renderPose.rotationPoint())
                .sub(this.singleBlockPos.getX(), this.singleBlockPos.getY(), this.singleBlockPos.getZ()));

        renderCenter.negate().add(renderX, renderY, renderZ);

        Matrix4f transform = LIMLIB_TRANSFORM.identity();
        transform.translate((float) (renderCenter.x() - camX), (float) (renderCenter.y() - camY), (float) (renderCenter.z() - camZ));
        transform.rotate(new Quaternionf(renderRot));

        poseStack.last().pose().mul(modelView).mul(transform);
        transform.normal(poseStack.last().normal());
    }
}
