package com.ninni.species.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.server.block.entity.HopelightBlockEntity;
import com.ninni.species.registry.SpeciesRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import static com.ninni.species.Species.MOD_ID;
import static com.ninni.species.server.block.HopelightBlock.*;

public class HopelightBlockEntityRenderer implements BlockEntityRenderer<HopelightBlockEntity> {

    public HopelightBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        // Constructor logic here
    }

    @Override
    public boolean shouldRenderOffScreen(HopelightBlockEntity p_112306_) {
        return true;
    }

    @Override
    public boolean shouldRender(HopelightBlockEntity p_173568_, Vec3 p_173569_) {
        return true;
    }

    public void render(HopelightBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        VertexConsumer builder = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(this.getTextureLocation()));


        float left = 0.0f;
        float right = 1.0f;

        Vec2[] vertices = new Vec2[]{
                new Vec2(left, 1),
                new Vec2(right, 1),
                new Vec2(right, 1),
                new Vec2(left, 1),
                new Vec2(right, -3),
                new Vec2(left, -3)
        };

        Vec2[] vertices2 = new Vec2[]{
                new Vec2(left, 0),
                new Vec2(right, 0),
                new Vec2(right, 0),
                new Vec2(left, 0),
                new Vec2(right, 4),
                new Vec2(left, 4)
        };

        int r = (blockEntity.getColor() & 0xFF0000) >> 16;
        int g = (blockEntity.getColor() & 0xFF00) >> 8;
        int b = (blockEntity.getColor() & 0xFF);

        if (blockEntity.getBlockState().getValue(BlockStateProperties.HANGING)) {

            if (!blockEntity.getBlockState().getValue(CONNECTED_Y)) {
                builder.vertex(poseStack.last().pose(), right, right, left)
                        .color(-r, -g, -b, 1F)
                        .uv(1F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), left, right, left)
                        .color(-r, -g, -b, 1F)
                        .uv(0F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), left, right, right)
                        .color(-r, -g, -b, 1F)
                        .uv(0.5F, 0.5F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), right, right, right)
                        .color(-r, -g, -b, 1F)
                        .uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
            }

            if (!blockEntity.getBlockState().getValue(NORTH)) {
                //FRONT
                builder.vertex(poseStack.last().pose(), vertices[3].x, vertices[3].y, left)
                        .color(-r,-g,-b,1F)
                        .uv(1F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices[2].x, vertices[2].y, left)
                        .color(-r,-g,-b,1F)
                        .uv(0F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices[4].x, vertices[4].y, left)
                        .color(r,g,b,0)
                        .uv(0.5F, 0.5F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices[5].x, vertices[5].y, left)
                        .color(r,g,b,0)
                        .uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
            }

            if (!blockEntity.getBlockState().getValue(SOUTH)) {
                //BACK
                builder.vertex(poseStack.last().pose(), vertices[3].x, vertices[3].y, right)
                        .color(-r,-g,-b,1F)
                        .uv(1F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices[2].x, vertices[2].y, right)
                        .color(-r,-g,-b,1F)
                        .uv(0F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices[4].x, vertices[4].y, right)
                        .color(r,g,b,0)
                        .uv(0.5F, 0.5F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices[5].x, vertices[5].y, right)
                        .color(r,g,b,0)
                        .uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
            }

            if (!blockEntity.getBlockState().getValue(WEST)) {
                //LEFT
                builder.vertex(poseStack.last().pose(), left, vertices[3].y, left)
                        .color(-r,-g,-b,1F)
                        .uv(1F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), left, vertices[2].y, right)
                        .color(-r,-g,-b,1F)
                        .uv(0F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), left, vertices[4].y, right)
                        .color(r,g,b,0)
                        .uv(0.5F, 0.5F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), left, vertices[5].y, left)
                        .color(r,g,b,0)
                        .uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
            }

            if (!blockEntity.getBlockState().getValue(EAST)) {
                //RIGHT
                builder.vertex(poseStack.last().pose(), right, vertices[3].y, left)
                        .color(-r,-g,-b,1F)
                        .uv(1F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), right, vertices[2].y, right)
                        .color(-r,-g,-b,1F)
                        .uv(0F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), right, vertices[4].y, right)
                        .color(r,g,b,0)
                        .uv(0.5F, 0.5F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), right, vertices[5].y, left)
                        .color(r,g,b,0)
                        .uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
            }
        } else {

            if (!blockEntity.getBlockState().getValue(CONNECTED_Y)) {
                builder.vertex(poseStack.last().pose(), right, left, left)
                        .color(-r, -g, -b, 1F)
                        .uv(1F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), left, left, left)
                        .color(-r, -g, -b, 1F)
                        .uv(0F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), left, left, right)
                        .color(-r, -g, -b, 1F)
                        .uv(0.5F, 0.5F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), right, left, right)
                        .color(-r, -g, -b, 1F)
                        .uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
            }
            if (!blockEntity.getBlockState().getValue(NORTH)) {
                //FRONT
                builder.vertex(poseStack.last().pose(), vertices2[3].x, vertices2[3].y, left)
                        .color(-r,-g,-b,1F)
                        .uv(1F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices2[2].x, vertices2[2].y, left)
                        .color(-r,-g,-b,1F)
                        .uv(0F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices2[4].x, vertices2[4].y, left)
                        .color(r,g,b,0)
                        .uv(0.5F, 0.5F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices2[5].x, vertices2[5].y, left)
                        .color(r,g,b,0)
                        .uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
            }

            if (!blockEntity.getBlockState().getValue(SOUTH)) {
                //BACK
                builder.vertex(poseStack.last().pose(), vertices2[3].x, vertices2[3].y, right)
                        .color(-r,-g,-b,1F)
                        .uv(1F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices2[2].x, vertices2[2].y, right)
                        .color(-r,-g,-b,1F)
                        .uv(0F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices2[4].x, vertices2[4].y, right)
                        .color(r,g,b,0)
                        .uv(0.5F, 0.5F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), vertices2[5].x, vertices2[5].y, right)
                        .color(r,g,b,0)
                        .uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
            }

            if (!blockEntity.getBlockState().getValue(WEST)) {
                //LEFT
                builder.vertex(poseStack.last().pose(), left, vertices2[3].y, left)
                        .color(-r,-g,-b,1F)
                        .uv(1F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), left, vertices2[2].y, right)
                        .color(-r,-g,-b,1F)
                        .uv(0F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), left, vertices2[4].y, right)
                        .color(r,g,b,0)
                        .uv(0.5F, 0.5F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), left, vertices2[5].y, left)
                        .color(r,g,b,0)
                        .uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
            }

            if (!blockEntity.getBlockState().getValue(EAST)) {
                //RIGHT
                builder.vertex(poseStack.last().pose(), right, vertices2[3].y, left)
                        .color(-r,-g,-b,1F)
                        .uv(1F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), right, vertices2[2].y, right)
                        .color(-r,-g,-b,1F)
                        .uv(0F, 0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), right, vertices2[4].y, right)
                        .color(r,g,b,0)
                        .uv(0.5F, 0.5F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
                builder.vertex(poseStack.last().pose(), right, vertices2[5].y, left)
                        .color(r,g,b,0)
                        .uv(1, 1)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                        .endVertex();
            }
        }
        poseStack.popPose();

    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(MOD_ID, "textures/block/spectre_glow_base.png");
    }
}