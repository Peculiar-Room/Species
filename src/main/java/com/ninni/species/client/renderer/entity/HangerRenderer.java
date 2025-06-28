package com.ninni.species.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.mob.update_3.HangerModel;
import com.ninni.species.server.entity.mob.update_3.Hanger;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import static com.ninni.species.Species.MOD_ID;

public abstract class HangerRenderer<T extends Hanger, M extends HangerModel<T>> extends MobRenderer<T, M> {
    public static final ResourceLocation TEXTURE_COIL = new ResourceLocation(MOD_ID, "textures/entity/hanger/coil/coil_tongue.png");

    public HangerRenderer(EntityRendererProvider.Context context, M model, float v) {
        super(context, model, v);
    }

    void drawTongueQuad(PoseStack poseStack, VertexConsumer builder, Vec3 base, Vec3 tip, Vec3 side, float vMax, int packedLight, float baseOpacity, float tipOpacity) {
        PoseStack.Pose pose = poseStack.last();

        builder.vertex(pose.pose(), (float)(base.x + side.x), (float)(base.y + side.y), (float)(base.z + side.z))
                .color(1F, 1F, 1F, baseOpacity)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        builder.vertex(pose.pose(), (float)(base.x - side.x), (float)(base.y - side.y), (float)(base.z - side.z))
                .color(1F, 1F, 1F, baseOpacity)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        builder.vertex(pose.pose(), (float)(tip.x - side.x), (float)(tip.y - side.y), (float)(tip.z - side.z))
                .color(1F, 1F, 1F, tipOpacity)
                .uv(1F, vMax)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        builder.vertex(pose.pose(), (float)(tip.x + side.x), (float)(tip.y + side.y), (float)(tip.z + side.z))
                .color(1F, 1F, 1F, tipOpacity)
                .uv(0F, vMax)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();
    }

    @Override
    public boolean shouldRender(T hanger, Frustum frustum, double v, double v1, double v2) {
        if (hanger.isTongueOut()) return true;
        return super.shouldRender(hanger, frustum, v, v1, v2);
    }
}
