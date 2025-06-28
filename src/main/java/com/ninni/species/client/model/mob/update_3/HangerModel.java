package com.ninni.species.client.model.mob.update_3;

import com.ninni.species.client.animation.DeflectorDummyAnimations;
import com.ninni.species.client.animation.HangerAnimations;
import com.ninni.species.server.entity.mob.update_3.Hanger;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public abstract class HangerModel<T extends Hanger> extends HierarchicalModel<T> {
    public final ModelPart root;
    public final ModelPart all;
    public final ModelPart body;
    public final ModelPart rightFrontLeg2;
    public final ModelPart leftFrontLeg2;
    public final ModelPart leftBackLeg2;
    public final ModelPart rightBackLeg2;
    public final ModelPart rightFrontLeg;
    public final ModelPart rightBackLeg;
    public final ModelPart leftBackLeg;
    public final ModelPart leftFrontLeg;
    public final ModelPart head;
    public final ModelPart rightFrontEyes;
    public final ModelPart rightBackEyes;
    public final ModelPart leftBackEyes;
    public final ModelPart leftFrontEyes;
    public final ModelPart rightMouth;
    public final ModelPart leftMouth;

    public HangerModel(ModelPart root) {
        this.root = root;
        this.all = root.getChild("all");
        this.body = this.all.getChild("body");
        this.rightFrontLeg2 = this.body.getChild("rightFrontLeg2");
        this.leftFrontLeg2 = this.body.getChild("leftFrontLeg2");
        this.leftBackLeg2 = this.body.getChild("leftBackLeg2");
        this.rightBackLeg2 = this.body.getChild("rightBackLeg2");
        this.rightFrontLeg = this.body.getChild("rightFrontLeg");
        this.rightBackLeg = this.body.getChild("rightBackLeg");
        this.leftBackLeg = this.body.getChild("leftBackLeg");
        this.leftFrontLeg = this.body.getChild("leftFrontLeg");
        this.head = this.all.getChild("head");
        this.rightFrontEyes = this.head.getChild("rightFrontEyes");
        this.rightBackEyes = this.head.getChild("rightBackEyes");
        this.leftBackEyes = this.head.getChild("leftBackEyes");
        this.leftFrontEyes = this.head.getChild("leftFrontEyes");
        this.rightMouth = this.head.getChild("rightMouth");
        this.leftMouth = this.head.getChild("leftMouth");
    }


    @Override
    public ModelPart root() {
        return this.root;
    }
    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(HangerAnimations.WALK, limbAngle, limbDistance, 2f, 8.0f);
        this.animate(entity.eyeTwitchAnimationState, HangerAnimations.EYE_TWITCH, animationProgress);
        this.animate(entity.lookAroundAnimationState, HangerAnimations.LOOK_AROUND, animationProgress);
        this.animate(entity.shiftAnimationState, HangerAnimations.SHIFT, animationProgress);

        this.head.y += Mth.sin(animationProgress * 0.1F) * 0.2F - 0.2F;
        this.head.zRot += Mth.cos(animationProgress * 0.1F) * 0.025F;

        this.leftFrontEyes.zRot += Mth.sin(animationProgress * 0.1F) * 0.05F;
        this.leftFrontEyes.xRot += Mth.cos(animationProgress * 0.1F) * 0.05F - 0.05F;
        this.rightFrontEyes.zRot += Mth.sin(animationProgress * 0.1F - 0.3F) * 0.05F;
        this.rightFrontEyes.xRot += Mth.cos(animationProgress * 0.1F - 0.3F) * 0.05F - 0.05F;
        this.leftBackEyes.zRot += Mth.cos(animationProgress * 0.1F - 0.3F) * 0.05F;
        this.leftBackEyes.xRot += Mth.sin(animationProgress * 0.1F - 0.3F) * 0.05F + 0.05F;
        this.rightBackEyes.zRot += Mth.cos(animationProgress * 0.1F - 0.6F) * 0.05F;
        this.rightBackEyes.xRot += Mth.sin(animationProgress * 0.1F - 0.6F) * 0.05F + 0.05F;

        float mouthAngle;

        if (entity.getJawSnapTicks() > 0) {
            if (entity.getJawSnapTicks() > 6) mouthAngle = 0;
            else mouthAngle = ((10 - entity.getJawSnapTicks()) / 10.0F - 0.4F);
        } else {
            mouthAngle = entity.mouthOpenProgress;
        }

        if (entity.getJawSnapTicks() > 0) {
            float shake = Mth.sin(animationProgress * 40F) * 0.05F;
            this.rightMouth.y += shake;
            this.leftMouth.y += shake;
        }

        this.rightMouth.zRot -= mouthAngle;
        this.leftMouth.zRot += mouthAngle;
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 23.5F, -3.0F));

        PartDefinition body = all.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 3.0F));

        PartDefinition rightFrontLeg2 = body.addOrReplaceChild("rightFrontLeg2", CubeListBuilder.create().texOffs(44, 34).addBox(-1.0F, 0.0F, -16.0F, 2.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, -5.0F, 0.0873F, 0.7854F, 0.0F));

        PartDefinition leftFrontLeg2 = body.addOrReplaceChild("leftFrontLeg2", CubeListBuilder.create().texOffs(44, 34).mirror().addBox(-1.0F, 0.0F, -16.0F, 2.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, -1.0F, -5.0F, 0.0873F, -0.7854F, 0.0F));

        PartDefinition leftBackLeg2 = body.addOrReplaceChild("leftBackLeg2", CubeListBuilder.create().texOffs(64, 32).mirror().addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.0F, -1.0F, 5.0F, -0.0873F, 0.7854F, 0.0F));

        PartDefinition rightBackLeg2 = body.addOrReplaceChild("rightBackLeg2", CubeListBuilder.create().texOffs(64, 32).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, 5.0F, -0.0873F, -0.7854F, 0.0F));

        PartDefinition rightFrontLeg = body.addOrReplaceChild("rightFrontLeg", CubeListBuilder.create().texOffs(44, 34).addBox(-1.0F, 0.0F, -16.0F, 2.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, -5.0F, 0.0873F, 0.3927F, 0.0F));

        PartDefinition rightBackLeg = body.addOrReplaceChild("rightBackLeg", CubeListBuilder.create().texOffs(64, 32).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -1.0F, 5.0F, -0.0873F, -0.3927F, 0.0F));

        PartDefinition leftBackLeg = body.addOrReplaceChild("leftBackLeg", CubeListBuilder.create().texOffs(64, 32).mirror().addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, -1.0F, 5.0F, -0.0873F, 0.3927F, 0.0F));

        PartDefinition leftFrontLeg = body.addOrReplaceChild("leftFrontLeg", CubeListBuilder.create().texOffs(44, 34).mirror().addBox(-1.0F, 0.0F, -16.0F, 2.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, -1.0F, -5.0F, 0.0873F, -0.3927F, 0.0F));

        PartDefinition head = all.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 50).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 3.0F));

        PartDefinition rightFrontEyes = head.addOrReplaceChild("rightFrontEyes", CubeListBuilder.create().texOffs(-8, 80).addBox(-13.0F, 0.0F, -5.0F, 13.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -2.0F, -5.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition rightBackEyes = head.addOrReplaceChild("rightBackEyes", CubeListBuilder.create().texOffs(-8, 88).addBox(-13.0F, 0.0F, -3.0F, 13.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -2.0F, 5.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition leftBackEyes = head.addOrReplaceChild("leftBackEyes", CubeListBuilder.create().texOffs(-8, 88).mirror().addBox(0.0F, 0.0F, -3.0F, 13.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.0F, -2.0F, 5.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition leftFrontEyes = head.addOrReplaceChild("leftFrontEyes", CubeListBuilder.create().texOffs(-8, 80).mirror().addBox(0.0F, 0.0F, -5.0F, 13.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.0F, -2.0F, -5.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition rightMouth = head.addOrReplaceChild("rightMouth", CubeListBuilder.create(), PartPose.offset(-2.0F, -4.0F, 0.0F));

        PartDefinition cube_r1 = rightMouth.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(5.0F, -2.0F, -1.0F, 20.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).mirror().addBox(5.0F, -5.0F, -1.0F, 20.0F, 3.0F, 10.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(2.0F, 5.0F, -4.0F, 0.0F, 0.0F, -1.5708F));

        PartDefinition leftMouth = head.addOrReplaceChild("leftMouth", CubeListBuilder.create(), PartPose.offset(2.0F, -4.0F, 0.0F));

        PartDefinition cube_r2 = leftMouth.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 13).mirror().addBox(5.0F, 1.0F, -1.0F, 20.0F, 3.0F, 10.0F, new CubeDeformation(0.01F)).mirror(false)
                .texOffs(0, 38).mirror().addBox(5.0F, -1.0F, -1.0F, 20.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.0F, 5.0F, -4.0F, 0.0F, 0.0F, -1.5708F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
}
