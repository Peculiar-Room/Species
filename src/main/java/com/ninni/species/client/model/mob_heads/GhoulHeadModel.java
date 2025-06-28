package com.ninni.species.client.model.mob_heads;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhoulHeadModel extends MobHeadModelBase {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart leftEar;
    private final ModelPart rightEar;

    public GhoulHeadModel(ModelPart root) {
        this.root = root;
        this.head = this.root.getChild("head");
        this.leftEar = this.head.getChild("leftEar");
        this.rightEar = this.head.getChild("rightEar");
    }

    @Override
    public void setupAnim(float limbSwing, float headY, float headX) {
        this.head.yRot = headY * ((float)Math.PI / 180F);
        this.head.xRot = headX * ((float)Math.PI / 180F);
        this.head.y = -3.5F;
        this.leftEar.yRot = (float)(-(Math.cos((limbSwing * 0.6F)))) * 0.5F;
        this.rightEar.yRot = (float)(Math.cos((limbSwing * 0.6F))) * 0.5F;
        this.leftEar.xRot = (float)(-(Math.sin((limbSwing * 0.6F)))) * 0.3F;
        this.rightEar.xRot = (float)(Math.sin((limbSwing * 0.6F))) * 0.3F;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 26).addBox(-6.0F, -3.5F, -4.0F, 12.0F, 7.0F, 8.0F), PartPose.offset(0.0F, 20.5F, 0.0F));
        head.addOrReplaceChild("leftEar", CubeListBuilder.create().texOffs(38, 0).addBox(-2.0F, -6.0F, 0.0F, 7.0F, 8.0F, 0.0F), PartPose.offset(6.0F, -1.5F, 2.0F));
        head.addOrReplaceChild("rightEar", CubeListBuilder.create().texOffs(38, 0).mirror().addBox(-5.0F, -6.0F, 0.0F, 7.0F, 8.0F, 0.0F).mirror(false), PartPose.offset(-6.0F, -1.5F, 2.0F));

        return LayerDefinition.create(meshdefinition, 64, 48);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, float v, float v1, float v2, float v3) {
        this.root.render(poseStack, vertexConsumer, i, i1, v, v1, v2, v3);
    }
}
