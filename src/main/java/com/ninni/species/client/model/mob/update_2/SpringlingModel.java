package com.ninni.species.client.model.mob.update_2;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.server.entity.mob.update_2.Springling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class SpringlingModel<T extends Springling> extends HierarchicalModel<T> {
    private final ModelPart root;
    public final ModelPart body;
    public final ModelPart head;
    private final ModelPart neck;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart tail;

    public SpringlingModel(ModelPart root) {
        this.root = root;

        body = root.getChild("body");
        head = root.getChild("head");
        neck = root.getChild("neck");
        tail = body.getChild("tail");
        rightLeg = body.getChild("rightLeg");
        leftLeg = body.getChild("leftLeg");
        rightArm = body.getChild("rightArm");
        leftArm = body.getChild("leftArm");

    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        limbDistance = Mth.clamp(limbDistance, -0.25F, 0.25F);
        float partialTicks = Minecraft.getInstance().getFrameTime();
        float interpolatedStep = Mth.lerp(partialTicks, entity.previousQuantizedStep, entity.currentQuantizedStep) / 10.0f;

        this.rightLeg.xRot = Mth.cos(limbAngle * 0.6662f) * 3.4f * limbDistance;
        this.leftLeg.xRot = Mth.cos(limbAngle * 0.6662f + (float)Math.PI) * 3.4f * limbDistance;
        this.tail.yRot = Mth.cos(limbAngle * 0.6F) * 2.4F * limbDistance;
        this.tail.xRot = - 0.5f;
        this.head.y = - 8.5F - interpolatedStep * 23.5f;
        this.leftArm.y = Mth.cos(animationProgress * 0.15F + (float)Math.PI / 2 + 2f) * 0.5f -11.5F;
        this.rightArm.y = Mth.cos(animationProgress * 0.15F + (float)Math.PI / 2) * 0.5f -11.5F;
        this.leftArm.xRot = Mth.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.rightArm.xRot = Mth.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        if (entity.isVehicle() || entity.getExtendedAmount() > 0) {
            this.head.xRot = 0;
            this.head.yRot = interpolatedStep * 2;
        } else {
            this.head.xRot = headPitch * ((float) Math.PI / 180f);
            this.head.yRot = headYaw * ((float) Math.PI / 180f);
        }
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-8.0F, -1.5F, -8.0F, 16.0F, 4.0F, 16.0F)
                        .texOffs(32, 20)
                        .mirror()
                        .addBox(-12.0F, -1.5F, -2.0F, 4.0F, 3.0F, 4.0F)
                        .mirror(false)
                        .texOffs(32, 20)
                        .addBox(8.0F, -1.5F, -2.0F, 4.0F, 3.0F, 4.0F),
                PartPose.offset(0.0F, -9.5F, 0.0F)
        );

        PartDefinition neck = partdefinition.addOrReplaceChild(
                "neck",
                CubeListBuilder.create()
                        .texOffs(0, 39)
                        .addBox(-2.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F),
                PartPose.offset(0.0F, 7.0F, 0.0F)
        );

        PartDefinition body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 20)
                        .addBox(-6.0F, -17.0F, -4.0F, 12.0F, 11.0F, 8.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        PartDefinition leftLeg = body.addOrReplaceChild(
                "leftLeg",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F),
                PartPose.offset(3.0F, -6.0F, -1.0F)
        );

        PartDefinition rightLeg = body.addOrReplaceChild(
                "rightLeg",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(-2.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F)
                        .mirror(false),
                PartPose.offset(-3.0F, -6.0F, -1.0F)
        );

        PartDefinition leftArm = body.addOrReplaceChild(
                "leftArm",
                CubeListBuilder.create()
                        .texOffs(16, 39)
                        .addBox(-1.5F, -2.5F, -6.0F, 3.0F, 5.0F, 6.0F),
                PartPose.offset(5.5F, -11.5F, -1.0F)
        );

        PartDefinition rightArm = body.addOrReplaceChild(
                "rightArm",
                CubeListBuilder.create()
                        .texOffs(16, 39)
                        .mirror()
                        .addBox(-1.5F, -2.5F, -6.0F, 3.0F, 5.0F, 6.0F)
                        .mirror(false),
                PartPose.offset(-5.5F, -11.5F, -1.0F)
        );

        PartDefinition tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(30, 29)
                        .addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 10.0F),
                PartPose.offset(0.0F, -8.0F, 4.0F)
        );

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
        if (this.young) {
            float l;
            poseStack.pushPose();
            l = 1.5f / 4;
            poseStack.scale(l, l, l);

            poseStack.translate(0.0f, 3.15f, 0f);
            this.head.render(poseStack, vertexConsumer, i, j, f, g, h, k);
            poseStack.popPose();
            poseStack.pushPose();
            l = 1.0f / 4;
            poseStack.scale(l, l, l);
            poseStack.translate(0.0f, 4.5f, 0.0f);
            this.body.render(poseStack, vertexConsumer, i, j, f, g, h, k);
            this.neck.render(poseStack, vertexConsumer, i, j, f, g, h, k);
            poseStack.popPose();
        } else {
            this.head.render(poseStack, vertexConsumer, i, j, f, g, h, k);
            this.body.render(poseStack, vertexConsumer, i, j, f, g, h, k);
            this.neck.render(poseStack, vertexConsumer, i, j, f, g, h, k);
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

}