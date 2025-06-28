package com.ninni.species.client.model.mob.update_3;

import com.ninni.species.client.animation.BewereagerAnimations;
import com.ninni.species.server.entity.mob.update_3.Bewereager;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class BewereagerModel<T extends Bewereager> extends ColorableHierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart all;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart torso;
    private final ModelPart tail;
    private final ModelPart chest;
    private final ModelPart head;
    private final ModelPart collar;
    private final ModelPart lowerJaw;
    private final ModelPart upperJaw;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart nose;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public BewereagerModel(ModelPart root) {
        this.root = root;
        this.all = root.getChild("all");
        this.leftLeg = this.all.getChild("leftLeg");
        this.rightLeg = this.all.getChild("rightLeg");
        this.torso = this.all.getChild("torso");
        this.tail = this.torso.getChild("tail");
        this.chest = this.all.getChild("chest");
        this.head = this.chest.getChild("head");
        this.collar = this.head.getChild("collar");
        this.lowerJaw = this.head.getChild("lowerJaw");
        this.upperJaw = this.head.getChild("upperJaw");
        this.leftEar = this.upperJaw.getChild("leftEar");
        this.rightEar = this.upperJaw.getChild("rightEar");
        this.nose = this.upperJaw.getChild("nose");
        this.leftArm = this.all.getChild("leftArm");
        this.rightArm = this.all.getChild("rightArm");
    }


    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.head.xRot = headPitch * ((float)Math.PI / 180);
        this.head.yRot = headYaw * ((float)Math.PI / 180);
        this.rightArm.zRot += 1 * (Mth.cos(animationProgress * 0.09F) * 0.05F + 0.05F);
        this.rightArm.xRot += 1 * Mth.sin(animationProgress * 0.067F) * 0.05F;
        this.leftArm.zRot += -1 * (Mth.cos(animationProgress * 0.09F) * 0.05F + 0.05F);
        this.leftArm.xRot += -1 * Mth.sin(animationProgress * 0.067F) * 0.05F;
        this.tail.zRot += 1 * Mth.cos((animationProgress * 0.125F)+  0.5F) * 0.25F;
        this.tail.xRot += 2 * Mth.sin((animationProgress * 0.125F)+  0.5F) * 0.05F;
        this.tail.yRot += 1 * Mth.sin(animationProgress * 0.125F) * 0.25F;


        this.animateWalk(BewereagerAnimations.WALK, limbAngle, limbDistance, 2, 100);
        this.animate(entity.howlAnimationState, BewereagerAnimations.HOWL, animationProgress);
        this.animate(entity.biteAttackAnimationState, BewereagerAnimations.BITE, animationProgress);
        this.animate(entity.slashAttackAnimationState, BewereagerAnimations.SLASH, animationProgress);
        this.animate(entity.shakeAnimationState, BewereagerAnimations.SHAKE, animationProgress);
        this.animate(entity.splitAnimationState, BewereagerAnimations.SPLIT, animationProgress);
        this.animate(entity.stunAnimationState, BewereagerAnimations.STUN, animationProgress);
        this.animate(entity.jumpAnimationState, BewereagerAnimations.JUMP, animationProgress);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition leftLeg = all.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(64, 55).addBox(-1.5F, 0.5F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -11.5F, 0.5F));

        PartDefinition rightLeg = all.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(64, 55).mirror().addBox(-1.5F, 0.5F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, -11.5F, 0.5F));

        PartDefinition torso = all.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(50, 0).addBox(-4.5F, -3.5F, -3.5F, 9.0F, 7.0F, 7.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, -14.5F, 0.5F));

        PartDefinition tail = torso.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(67, 28).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(89, 28).addBox(-1.0F, 2.0F, 0.0F, 2.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.5F, 3.5F, -0.7854F, 0.0F, 0.0F));

        PartDefinition chest = all.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, -13.0F, -12.0F, 13.0F, 13.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 25).addBox(-6.5F, 0.0F, -12.0F, 13.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(96, -16).addBox(0.0F, -17.0F, -12.0F, 0.0F, 11.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, 4.0F));

        PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 55).addBox(-4.5F, -5.0F, -2.0F, 9.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.5F, -12.0F));

        PartDefinition collar = head.addOrReplaceChild("collar", CubeListBuilder.create().texOffs(22, 55).addBox(-4.5F, -5.0F, -1.0F, 9.0F, 10.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, -1.0F));

        PartDefinition lowerJaw = head.addOrReplaceChild("lowerJaw", CubeListBuilder.create().texOffs(58, 15).addBox(-4.5F, 1.0F, -8.0F, 9.0F, 0.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(34, 41).addBox(-4.5F, -2.0F, -8.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 1.0F, -2.0F));

        PartDefinition upperJaw = head.addOrReplaceChild("upperJaw", CubeListBuilder.create().texOffs(0, 41).addBox(-4.5F, -4.0F, -8.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.02F))
                .texOffs(58, 15).mirror().addBox(-4.5F, -1.0F, -8.0F, 9.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -1.0F, -2.0F));

        PartDefinition leftEar = upperJaw.addOrReplaceChild("leftEar", CubeListBuilder.create().texOffs(56, 65).addBox(0.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offset(4.5F, -3.0F, -1.5F));

        PartDefinition rightEar = upperJaw.addOrReplaceChild("rightEar", CubeListBuilder.create().texOffs(56, 65).mirror().addBox(-2.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.5F, -3.0F, -1.5F));

        PartDefinition nose = upperJaw.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(44, 65).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -7.0F));

        PartDefinition leftArm = all.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(72, 0).addBox(-2.5F, 17.0F, -2.0F, 3.0F, 0.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 67).addBox(0.5F, -3.0F, -2.0F, 3.0F, 20.0F, 5.0F, new CubeDeformation(0.25F))
                .texOffs(50, 14).addBox(0.5F, -3.0F, -2.0F, 3.0F, 20.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -22.0F, -5.0F));

        PartDefinition rightArm = all.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(72, 0).mirror().addBox(0.5F, 17.0F, -2.0F, 3.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(16, 67).mirror().addBox(-2.5F, -3.0F, -2.0F, 3.0F, 20.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false)
                .texOffs(50, 14).mirror().addBox(-2.5F, -3.0F, -2.0F, 3.0F, 20.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, -22.0F, -5.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

}
