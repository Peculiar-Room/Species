package com.ninni.species.client.model.mob_heads;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QuakeHeadModel extends MobHeadModelBase {
    private final ModelPart root;
    private final ModelPart head;

    public QuakeHeadModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
    }

    @Override
    public void setupAnim(float limbSwing, float headY, float headX) {
        this.head.yRot = headY * ((float)Math.PI / 180F);
        this.head.xRot = headX * ((float)Math.PI / 180F);
        this.head.y = 0;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(86, 49).addBox(-6.5F, -7.0F, -5.0F, 13.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 160, 112);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, float v, float v1, float v2, float v3) {
        this.root.render(poseStack, vertexConsumer, i, i1, v, v1, v2, v3);
    }
}
