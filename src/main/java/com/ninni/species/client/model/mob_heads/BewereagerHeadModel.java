package com.ninni.species.client.model.mob_heads;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BewereagerHeadModel extends MobHeadModelBase {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart collar;
    private final ModelPart lowerJaw;
    private final ModelPart upperJaw;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart nose;

    public BewereagerHeadModel(ModelPart root) {
        this.root = root;
        this.head = this.root.getChild("head");
        this.collar = this.head.getChild("collar");
        this.lowerJaw = this.head.getChild("lowerJaw");
        this.upperJaw = this.head.getChild("upperJaw");
        this.leftEar = this.upperJaw.getChild("leftEar");
        this.rightEar = this.upperJaw.getChild("rightEar");
        this.nose = this.upperJaw.getChild("nose");
    }

    @Override
    public void setupAnim(float limbSwing, float headY, float headX) {
        this.head.yRot = headY * ((float)Math.PI / 180F);
        this.head.xRot = headX * ((float)Math.PI / 180F);
        this.root.y = -24;
        if (limbSwing == 0) {
            this.lowerJaw.xRot = 0;
            this.upperJaw.xRot = 0;
        } else {
            this.lowerJaw.xRot = (float)(-(Math.sin((limbSwing * 0.6F)))) * 0.3F + 0.3F;
            this.upperJaw.xRot = (float)(Math.sin((limbSwing * 0.6F))) * 0.3F - 0.3F;
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 55).addBox(-4.5F, -10.0F, 3.5F, 9.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        head.addOrReplaceChild("collar", CubeListBuilder.create().texOffs(22, 55).addBox(-4.5F, -5.0F, -1.0F, 9.0F, 10.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -5.0F, 4.5F));
        head.addOrReplaceChild("lowerJaw", CubeListBuilder.create().texOffs(58, 15).addBox(-4.5F, 1.0F, -8.0F, 9.0F, 0.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(34, 41).addBox(-4.5F, -2.0F, -8.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -4.0F, 3.5F));
        PartDefinition upperJaw = head.addOrReplaceChild("upperJaw", CubeListBuilder.create().texOffs(0, 41).addBox(-4.5F, -4.0F, -8.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.02F))
                .texOffs(58, 15).mirror().addBox(-4.5F, -1.0F, -8.0F, 9.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -6.0F, 3.5F));
        upperJaw.addOrReplaceChild("leftEar", CubeListBuilder.create().texOffs(56, 65).addBox(0.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offset(4.5F, -3.0F, -1.5F));
        upperJaw.addOrReplaceChild("rightEar", CubeListBuilder.create().texOffs(56, 65).mirror().addBox(-2.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.5F, -3.0F, -1.5F));
        upperJaw.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(44, 65).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -7.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }


    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, float v, float v1, float v2, float v3) {
        this.root.render(poseStack, vertexConsumer, i, i1, v, v1, v2, v3);
    }
}
