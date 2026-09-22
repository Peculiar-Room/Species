package com.ninni.species.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.registry.SpeciesRenderTypes;
import com.ninni.species.server.block.entity.ChaindelierBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import static com.ninni.species.Species.MOD_ID;

public class ChaindelierBlockEntityRenderer implements BlockEntityRenderer<ChaindelierBlockEntity> {

    public ChaindelierBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        // Constructor logic here
    }

    @Override
    public boolean shouldRenderOffScreen(ChaindelierBlockEntity p_112306_) {
        return true;
    }

    @Override
    public boolean shouldRender(ChaindelierBlockEntity p_173568_, Vec3 p_173569_) {
        return true;
    }

    public void render(ChaindelierBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        VertexConsumer builder = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/block/spectralibur_glow.png")));

        float left = 0.125f;
        float right = 0.875f;

        Vec2[] vertices = new Vec2[]{
                new Vec2(left, 1),
                new Vec2(right, 1),
                new Vec2(right, 0.75f),
                new Vec2(left, 0.75f),
                new Vec2(right, -3),
                new Vec2(left, -3)
        };

        //FRONT
        builder.addVertex(poseStack.last().pose(), vertices[3].x, vertices[3].y, left)
                .setColor(1.0F, 1, 1, 1.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[2].x, vertices[2].y, left)
                .setColor(1.0F, 1, 1, 1.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[4].x, vertices[4].y, left)
                .setColor(1.0F, 1, 1, 0.0F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[5].x, vertices[5].y, left)
                .setColor(1.0F, 1, 1, 0.0F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);

        //BACK
        builder.addVertex(poseStack.last().pose(), vertices[3].x, vertices[3].y, right)
                .setColor(1.0F, 1, 1, 1.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[2].x, vertices[2].y, right)
                .setColor(1.0F, 1, 1, 1.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[4].x, vertices[4].y, right)
                .setColor(1.0F, 1, 1, 0.0F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), vertices[5].x, vertices[5].y, right)
                .setColor(1.0F, 1, 1, 0.0F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);

        //LEFT
        builder.addVertex(poseStack.last().pose(), left, vertices[3].y, left)
                .setColor(1.0F, 1, 1, 1.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), left, vertices[2].y, right)
                .setColor(1.0F, 1, 1, 1.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), left, vertices[4].y, right)
                .setColor(1.0F, 1, 1, 0.0F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), left, vertices[5].y, left)
                .setColor(1.0F, 1, 1, 0.0F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);

        //RIGHT
        builder.addVertex(poseStack.last().pose(), right, vertices[3].y, left)
                .setColor(1.0F, 1, 1, 1.0F)
                .setUv(1F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), right, vertices[2].y, right)
                .setColor(1.0F, 1, 1, 1.0F)
                .setUv(0F, 0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), right, vertices[4].y, right)
                .setColor(1.0F, 1, 1, 0.0F)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        builder.addVertex(poseStack.last().pose(), right, vertices[5].y, left)
                .setColor(1.0F, 1, 1, 0.0F)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(15728880)
                .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        poseStack.popPose();
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(ChaindelierBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY() - 4, pos.getZ(), pos.getX() + 1.0, pos.getY() + 1, pos.getZ() + 1.0);
    }
}