package com.ninni.species.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.mob.update_3.CliffHangerModel;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.server.entity.mob.update_3.CliffHanger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class CliffHangerRenderer extends HangerRenderer<CliffHanger, CliffHangerModel<CliffHanger>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/hanger/cliff_hanger.png");
    public static final ResourceLocation TEXTURE_RARE = new ResourceLocation(MOD_ID, "textures/entity/hanger/drip_hanger.png");

    public CliffHangerRenderer(EntityRendererProvider.Context context) {
        super(context, new CliffHangerModel<>(context.bakeLayer(SpeciesEntityModelLayers.CLIFF_HANGER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CliffHanger entity) {
        return entity.isRare() ? TEXTURE_RARE : TEXTURE;
    }

    @Override
    public void render(CliffHanger entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_COIL));

        Vec3 base = new Vec3(0, entity.getTongueOffset(), 0);
        Vec3 tip = new Vec3(entity.getCurrentTonguePos());
        Vec3 direction = tip.subtract(base);
        double length = direction.length();

        if (!entity.isAlive()) return;

        Vec3 dirNorm = direction.normalize();
        Vec3 up = Math.abs(dirNorm.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 right = dirNorm.cross(up).normalize().scale(0.3);
        Vec3 forward = dirNorm.cross(right).normalize().scale(0.3);

        poseStack.pushPose();
        float vMax = (float) length;

        drawTongueQuad(poseStack, builder, base, tip, right, vMax, packedLight, 1, 1);
        drawTongueQuad(poseStack, builder, base, tip, forward, vMax, packedLight, 1, 1);

        if (entity.isAttached()) {
            poseStack.translate(-0.4F,-0.8F, -0.4F);
            poseStack.translate(tip.x, tip.y, tip.z);
            poseStack.scale(0.8F, 0.8F, 0.8F);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN), poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        } else {
            poseStack.translate(-0.4F,0, -0.4F);
            poseStack.translate(tip.x, tip.y, tip.z);
            poseStack.scale(0.8F, 0.8F, 0.8F);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.POINTED_DRIPSTONE.defaultBlockState(), poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();

    }

}
