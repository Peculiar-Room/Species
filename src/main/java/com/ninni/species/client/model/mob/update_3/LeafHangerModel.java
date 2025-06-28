package com.ninni.species.client.model.mob.update_3;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.server.entity.mob.update_3.LeafHanger;
import net.minecraft.client.model.geom.ModelPart;

public class LeafHangerModel<T extends LeafHanger> extends HangerModel<T> {
    private LeafHanger leafHanger;

    public LeafHangerModel(ModelPart root) {
        super(root);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        leafHanger = entity;
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, float v, float v1, float v2, float v3) {
        float opacity = leafHanger.isTongueOut() && !leafHanger.isPullingTarget() ? 0.4F : 1;
        super.renderToBuffer(poseStack, vertexConsumer, i, i1, v, v1, v2, opacity);
    }
}
