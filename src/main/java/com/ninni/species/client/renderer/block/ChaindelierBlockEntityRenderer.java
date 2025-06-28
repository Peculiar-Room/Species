package com.ninni.species.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.server.block.entity.ChaindelierBlockEntity;
import com.ninni.species.registry.SpeciesRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
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
        VertexConsumer builder = bufferSource.getBuffer(SpeciesRenderTypes.spectreLight(new ResourceLocation(MOD_ID, "textures/block/spectralibur_glow.png")));

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
        builder.vertex(poseStack.last().pose(), vertices[3].x, vertices[3].y, left)
                .color(1.0F, 1, 1, 1.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), vertices[2].x, vertices[2].y, left)
                .color(1.0F, 1, 1, 1.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), vertices[4].x, vertices[4].y, left)
                .color(1.0F, 1, 1, 0.0F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), vertices[5].x, vertices[5].y, left)
                .color(1.0F, 1, 1, 0.0F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();

        //BACK
        builder.vertex(poseStack.last().pose(), vertices[3].x, vertices[3].y, right)
                .color(1.0F, 1, 1, 1.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), vertices[2].x, vertices[2].y, right)
                .color(1.0F, 1, 1, 1.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), vertices[4].x, vertices[4].y, right)
                .color(1.0F, 1, 1, 0.0F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), vertices[5].x, vertices[5].y, right)
                .color(1.0F, 1, 1, 0.0F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();

        //LEFT
        builder.vertex(poseStack.last().pose(), left, vertices[3].y, left)
                .color(1.0F, 1, 1, 1.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), left, vertices[2].y, right)
                .color(1.0F, 1, 1, 1.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), left, vertices[4].y, right)
                .color(1.0F, 1, 1, 0.0F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), left, vertices[5].y, left)
                .color(1.0F, 1, 1, 0.0F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();

        //RIGHT
        builder.vertex(poseStack.last().pose(), right, vertices[3].y, left)
                .color(1.0F, 1, 1, 1.0F)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), right, vertices[2].y, right)
                .color(1.0F, 1, 1, 1.0F)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), right, vertices[4].y, right)
                .color(1.0F, 1, 1, 0.0F)
                .uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        builder.vertex(poseStack.last().pose(), right, vertices[5].y, left)
                .color(1.0F, 1, 1, 0.0F)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
        poseStack.popPose();
    }
}