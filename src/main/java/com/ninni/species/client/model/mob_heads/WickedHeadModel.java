package com.ninni.species.client.model.mob_heads;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WickedHeadModel extends MobHeadModelBase {
    private final ModelPart root;
    private final ModelPart candle;

    public WickedHeadModel(ModelPart root) {
        this.root = root;
        this.candle = root.getChild("candle");
    }

    @Override
    public void setupAnim(float limbSwing, float headY, float headX) {
        this.candle.yRot = headY * ((float)Math.PI / 180F);
        this.candle.xRot = headX * ((float)Math.PI / 180F);
        this.candle.y = 0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition candle = partdefinition.addOrReplaceChild("candle", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.25F))
                .texOffs(0, 0).addBox(-2.0F, -9.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, float v, float v1, float v2, float v3) {
        this.root.render(poseStack, vertexConsumer, i, i1, v, v1, v2, v3);
    }
}
